#include "LocalHub.h"
#include <Arduino.h>
#include <ArduinoJson.h>

LocalHub* LocalHub::instance = nullptr;

LocalHub::LocalHub() : lastStatusPrint(0) {
    instance = this;
}

void LocalHub::begin() {
    Serial.println("\n\n===========================================");
    Serial.println("       LocalHub Starting...");
    Serial.printf("       Version: %s\n", Config::HUB_VERSION);
    Serial.println("===========================================\n");

    // Initialize WiFi
    Serial.println("[1/3] Initializing WiFi...");
    if (!wifi.begin()) {
        Serial.println("ERROR: WiFi initialization failed!");
        Serial.println("Check your WiFi credentials in Config.h");
        // Continue anyway - will retry in loop
    }

    // Initialize MQTT
    Serial.println("\n[2/3] Initializing MQTT...");
    mqtt.begin(Config::MQTT_BROKER, Config::MQTT_PORT);
    mqtt.setMessageCallback(onMQTTMessage);

    // Initialize BLE
    Serial.println("\n[3/3] Initializing BLE...");
    ble.begin();
    ble.setDeviceDiscoveredCallback(onDeviceDiscovered);
    ble.setDataReceivedCallback(onBLEDataReceived);

    Serial.println("\n===========================================");
    Serial.println("       LocalHub Ready!");
    Serial.println("===========================================\n");

    lastStatusPrint = millis();
}

void LocalHub::loop() {
    // Reconnect WiFi if needed
    wifi.reconnect();

    // Handle MQTT
    mqtt.loop();

    // Handle BLE
    ble.loop();

    // Periodic status print
    uint32_t now = millis();
    if (now - lastStatusPrint > STATUS_PRINT_INTERVAL_MS) {
        lastStatusPrint = now;
        printStatus();
    }

    delay(Config::LOOP_DELAY_MS);
}

void LocalHub::printStatus() {
    Serial.println("\n--- Hub Status ---");
    Serial.printf("WiFi: %s (IP: %s, RSSI: %d dBm)\n",
                 wifi.isConnected() ? "Connected" : "Disconnected",
                 wifi.getLocalIP().c_str(),
                 wifi.getRSSI());
    Serial.printf("MQTT: %s\n", mqtt.isConnected() ? "Connected" : "Disconnected");

    std::vector<String> connected = ble.getConnectedDevices();
    Serial.printf("BLE Devices: %d connected\n", connected.size());
    for (const auto& addr : connected) {
        Serial.printf("  - %s\n", addr.c_str());
    }

    Serial.printf("Uptime: %lu seconds\n", millis() / 1000);
    Serial.println("------------------\n");
}

// ====== Static Callback Handlers ======

void LocalHub::onDeviceDiscovered(const char* address, const char* name) {
    Serial.printf(">>> Device discovered: %s (%s)\n", name, address);

    if (instance && instance->mqtt.isConnected()) {
        instance->mqtt.publishBLEDiscovery(address, name);
    }
}

void LocalHub::onBLEDataReceived(const char* address, const char* characteristic, const uint8_t* data, size_t length) {
    Serial.printf(">>> BLE Data from %s [%s]: ", address, characteristic);
    for (size_t i = 0; i < length; i++) {
        Serial.printf("%02X ", data[i]);
    }
    Serial.println();

    if (instance && instance->mqtt.isConnected()) {
        instance->mqtt.publishBLEData(address, characteristic, data, length);
    }
}

void LocalHub::onMQTTMessage(const char* topic, const char* payload) {
    Serial.printf(">>> MQTT Message [%s]: %s\n", topic, payload);

    if (instance) {
        instance->handleMQTTControl(topic, payload);
    }
}

void LocalHub::handleMQTTControl(const char* topic, const char* payload) {
    JsonDocument doc;
    DeserializationError error = deserializeJson(doc, payload);

    if (error) {
        Serial.printf("JSON parsing failed: %s\n", error.c_str());
        return;
    }

    String topicStr(topic);

    // Handle BLE control commands
    if (topicStr == Config::MQTT_TOPIC_BLE_CONTROL) {
        const char* command = doc["command"];
        const char* address = doc["address"];

        if (!command) {
            Serial.println("No command in control message");
            return;
        }

        if (strcmp(command, "connect") == 0 && address) {
            Serial.printf("Connecting to device: %s\n", address);
            ble.connectToDevice(address);
        } else if (strcmp(command, "disconnect") == 0 && address) {
            Serial.printf("Disconnecting from device: %s\n", address);
            ble.disconnectDevice(address);
        } else if (strcmp(command, "scan_start") == 0) {
            Serial.println("Starting BLE scan");
            ble.startScan();
        } else if (strcmp(command, "scan_stop") == 0) {
            Serial.println("Stopping BLE scan");
            ble.stopScan();
        } else if (strcmp(command, "list") == 0) {
            std::vector<String> connected = ble.getConnectedDevices();
            Serial.printf("Connected devices: %d\n", connected.size());
            for (const auto& addr : connected) {
                Serial.printf("  - %s\n", addr.c_str());
            }
        } else {
            Serial.printf("Unknown command: %s\n", command);
        }
    }
    // Handle config commands
    else if (topicStr == Config::MQTT_TOPIC_CONFIG) {
        const char* action = doc["action"];

        if (action && strcmp(action, "status") == 0) {
            printStatus();
            mqtt.publishStatus("online");
        } else if (action && strcmp(action, "restart") == 0) {
            Serial.println("Restarting...");
            delay(1000);
            ESP.restart();
        }
    }
}
