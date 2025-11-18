#include "MQTTManager.h"
#include <Arduino.h>

MQTTManager::MessageCallback MQTTManager::userCallback = nullptr;

MQTTManager::MQTTManager()
    : mqttClient(wifiClient), broker(nullptr), port(0), lastReconnectAttempt(0) {}

bool MQTTManager::begin(const char* broker, uint16_t port) {
    this->broker = broker;
    this->port = port;

    Serial.printf("Initializing MQTT... Broker: %s:%d\n", broker, port);

    mqttClient.setServer(broker, port);
    mqttClient.setCallback(mqttCallback);
    mqttClient.setKeepAlive(60);
    mqttClient.setSocketTimeout(10);

    return connect();
}

bool MQTTManager::connect() {
    if (mqttClient.connected()) return true;

    Serial.print("Connecting to MQTT broker...");

    bool connected = false;
    if (strlen(Config::MQTT_USERNAME) > 0) {
        connected = mqttClient.connect(
            Config::MQTT_CLIENT_ID,
            Config::MQTT_USERNAME,
            Config::MQTT_PASSWORD
        );
    } else {
        connected = mqttClient.connect(Config::MQTT_CLIENT_ID);
    }

    if (connected) {
        Serial.println(" Connected!");

        // Subscribe to control topics
        mqttClient.subscribe(Config::MQTT_TOPIC_BLE_CONTROL);
        mqttClient.subscribe(Config::MQTT_TOPIC_CONFIG);

        // Publish online status
        publishStatus("online");

        return true;
    } else {
        Serial.printf(" Failed! State: %d\n", mqttClient.state());
        return false;
    }
}

void MQTTManager::loop() {
    if (!mqttClient.connected()) {
        uint32_t now = millis();
        if (now - lastReconnectAttempt > RECONNECT_INTERVAL_MS) {
            lastReconnectAttempt = now;
            connect();
        }
    } else {
        mqttClient.loop();
    }
}

bool MQTTManager::isConnected() {
    return mqttClient.connected();
}

bool MQTTManager::publish(const char* topic, const char* payload, bool retained) {
    if (!mqttClient.connected()) {
        Serial.println("MQTT not connected. Cannot publish.");
        return false;
    }

    bool result = mqttClient.publish(topic, payload, retained);
    if (result) {
        Serial.printf("Published to %s: %s\n", topic, payload);
    } else {
        Serial.printf("Failed to publish to %s\n", topic);
    }

    return result;
}

bool MQTTManager::publishBLEDiscovery(const char* deviceAddress, const char* deviceName) {
    JsonDocument doc;
    doc["type"] = "discovery";
    doc["address"] = deviceAddress;
    doc["name"] = deviceName;
    doc["timestamp"] = millis();

    String payload;
    serializeJson(doc, payload);

    return publish(Config::MQTT_TOPIC_BLE_DISCOVERED, payload.c_str());
}

bool MQTTManager::publishBLEData(const char* deviceAddress, const char* characteristic, const uint8_t* data, size_t length) {
    JsonDocument doc;
    doc["type"] = "data";
    doc["address"] = deviceAddress;
    doc["characteristic"] = characteristic;
    doc["timestamp"] = millis();

    // Convert byte array to hex string
    String hexData = "";
    for (size_t i = 0; i < length; i++) {
        char buf[3];
        sprintf(buf, "%02X", data[i]);
        hexData += buf;
    }
    doc["data"] = hexData;
    doc["length"] = length;

    String payload;
    serializeJson(doc, payload);

    String topic = String(Config::MQTT_TOPIC_BLE_DATA) + "/" + String(deviceAddress);
    return publish(topic.c_str(), payload.c_str());
}

bool MQTTManager::publishStatus(const char* status) {
    JsonDocument doc;
    doc["status"] = status;
    doc["version"] = Config::HUB_VERSION;
    doc["timestamp"] = millis();
    doc["uptime"] = millis() / 1000;

    String payload;
    serializeJson(doc, payload);

    return publish(Config::MQTT_TOPIC_STATUS, payload.c_str(), true);
}

void MQTTManager::setMessageCallback(MessageCallback callback) {
    userCallback = callback;
}

void MQTTManager::mqttCallback(char* topic, uint8_t* payload, unsigned int length) {
    // Null-terminate the payload
    char message[length + 1];
    memcpy(message, payload, length);
    message[length] = '\0';

    Serial.printf("MQTT Message [%s]: %s\n", topic, message);

    if (userCallback) {
        userCallback(topic, message);
    }
}
