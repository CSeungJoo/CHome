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
    Serial.printf("       Serial:  %s\n", Config::SERIAL_NUMBER);
    Serial.println("===========================================\n");

    // Initialize WiFi
    Serial.println("[1/3] Initializing WiFi...");
    if (!wifi.begin()) {
        Serial.println("ERROR: WiFi initialization failed!");
    }

    // Initialize MQTT
    Serial.println("\n[2/3] Initializing MQTT...");
    mqtt.begin(Config::MQTT_BROKER, Config::MQTT_PORT, Config::SERIAL_NUMBER);
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
    wifi.reconnect();
    mqtt.loop();
    ble.loop();

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
        JsonDocument doc;
        JsonObject payload = doc.to<JsonObject>();
        payload["address"] = address;
        payload["name"] = name;

        instance->mqtt.publishEvent("BLE_DISCOVERY", payload);
    }
}

void LocalHub::onBLEDataReceived(const char* address, const char* characteristic, const uint8_t* data, size_t length) {
    Serial.printf(">>> BLE Data from %s [%s]: ", address, characteristic);
    for (size_t i = 0; i < length; i++) {
        Serial.printf("%02X ", data[i]);
    }
    Serial.println();

    if (instance && instance->mqtt.isConnected()) {
        JsonDocument doc;
        JsonObject payload = doc.to<JsonObject>();
        payload["address"] = address;
        payload["characteristic"] = characteristic;

        String hexData = "";
        for (size_t i = 0; i < length; i++) {
            char buf[3];
            sprintf(buf, "%02X", data[i]);
            hexData += buf;
        }
        payload["data"] = hexData;
        payload["length"] = length;

        instance->mqtt.publishEvent("BLE_DATA", payload);
    }
}

void LocalHub::onMQTTMessage(const char* topic, const char* payload) {
    Serial.printf(">>> Command received [%s]\n", topic);

    if (instance) {
        instance->handleCommand(payload);
    }
}

// ====== Command Handler — 백엔드 HubMessage 포맷 ======
// { kind: "COMMAND", type: "BLE_SCAN", requestId: "uuid", timestamp: epoch, payload: {} }

void LocalHub::handleCommand(const char* payload) {
    JsonDocument doc;
    DeserializationError error = deserializeJson(doc, payload);

    if (error) {
        Serial.printf("JSON parse failed: %s\n", error.c_str());
        return;
    }

    const char* kind = doc["kind"];
    const char* type = doc["type"];
    const char* requestId = doc["requestId"];

    if (!kind || strcmp(kind, "COMMAND") != 0) {
        Serial.println("Not a COMMAND message, ignoring");
        return;
    }

    if (!type || !requestId) {
        Serial.println("Missing type or requestId");
        return;
    }

    Serial.printf("Command: type=%s, requestId=%s\n", type, requestId);

    JsonObject cmdPayload = doc["payload"].as<JsonObject>();

    if (strcmp(type, "BLE_SCAN") == 0) {
        handleBLEScan(requestId);
    } else if (strcmp(type, "BLE_CONNECT") == 0) {
        handleBLEConnect(requestId, cmdPayload);
    } else if (strcmp(type, "BLE_DISCONNECT") == 0) {
        handleBLEDisconnect(requestId, cmdPayload);
    } else if (strcmp(type, "STATUS") == 0) {
        handleStatusRequest(requestId);
    } else if (strcmp(type, "RESTART") == 0) {
        JsonDocument resDoc;
        JsonObject resPayload = resDoc.to<JsonObject>();
        resPayload["message"] = "restarting";
        mqtt.publishResult(type, requestId, resPayload);
        delay(500);
        ESP.restart();
    } else {
        Serial.printf("Unknown command type: %s\n", type);
        JsonDocument resDoc;
        JsonObject resPayload = resDoc.to<JsonObject>();
        resPayload["error"] = "UNKNOWN_COMMAND";
        mqtt.publishResult(type, requestId, resPayload);
    }
}

void LocalHub::handleBLEScan(const char* requestId) {
    Serial.println("Starting BLE scan...");
    ble.startScan();

    JsonDocument doc;
    JsonObject payload = doc.to<JsonObject>();
    payload["message"] = "scan_started";

    mqtt.publishResult("BLE_SCAN", requestId, payload);
}

void LocalHub::handleBLEConnect(const char* requestId, JsonObject& cmdPayload) {
    const char* address = cmdPayload["address"];
    if (!address) {
        JsonDocument doc;
        JsonObject payload = doc.to<JsonObject>();
        payload["error"] = "address required";
        mqtt.publishResult("BLE_CONNECT", requestId, payload);
        return;
    }

    bool ok = ble.connectToDevice(address);

    JsonDocument doc;
    JsonObject payload = doc.to<JsonObject>();
    payload["address"] = address;
    payload["success"] = ok;

    mqtt.publishResult("BLE_CONNECT", requestId, payload);
}

void LocalHub::handleBLEDisconnect(const char* requestId, JsonObject& cmdPayload) {
    const char* address = cmdPayload["address"];
    if (!address) {
        JsonDocument doc;
        JsonObject payload = doc.to<JsonObject>();
        payload["error"] = "address required";
        mqtt.publishResult("BLE_DISCONNECT", requestId, payload);
        return;
    }

    bool ok = ble.disconnectDevice(address);

    JsonDocument doc;
    JsonObject payload = doc.to<JsonObject>();
    payload["address"] = address;
    payload["success"] = ok;

    mqtt.publishResult("BLE_DISCONNECT", requestId, payload);
}

void LocalHub::handleStatusRequest(const char* requestId) {
    JsonDocument doc;
    JsonObject payload = doc.to<JsonObject>();
    payload["wifi"] = wifi.isConnected();
    payload["ip"] = wifi.getLocalIP();
    payload["rssi"] = wifi.getRSSI();
    payload["mqtt"] = mqtt.isConnected();
    payload["uptime"] = millis() / 1000;
    payload["version"] = Config::HUB_VERSION;

    std::vector<String> devices = ble.getConnectedDevices();
    JsonArray bleArr = payload["bleDevices"].to<JsonArray>();
    for (const auto& addr : devices) {
        bleArr.add(addr);
    }

    mqtt.publishResult("STATUS", requestId, payload);
}
