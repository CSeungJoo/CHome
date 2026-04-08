#include "MQTTManager.h"
#include <Arduino.h>

MQTTManager::MessageCallback MQTTManager::userCallback = nullptr;

MQTTManager::MQTTManager()
    : mqttClient(wifiClient), broker(nullptr), port(0),
      lastReconnectAttempt(0), serialNumber(nullptr) {}

bool MQTTManager::begin(const char* broker, uint16_t port, const char* serialNumber) {
    this->broker = broker;
    this->port = port;
    this->serialNumber = serialNumber;

    // 토픽 조립: hub/{serialNumber}/command | result | event
    snprintf(commandTopic, sizeof(commandTopic), "hub/%s/command", serialNumber);
    snprintf(resultTopic, sizeof(resultTopic), "hub/%s/result", serialNumber);
    snprintf(eventTopic, sizeof(eventTopic), "hub/%s/event", serialNumber);

    Serial.printf("MQTT topics — cmd: %s, result: %s, event: %s\n",
                  commandTopic, resultTopic, eventTopic);

    mqttClient.setServer(broker, port);
    mqttClient.setCallback(mqttCallback);
    mqttClient.setKeepAlive(60);
    mqttClient.setSocketTimeout(10);
    mqttClient.setBufferSize(1024);

    return connect();
}

bool MQTTManager::connect() {
    if (mqttClient.connected()) return true;

    Serial.print("Connecting to MQTT broker...");

    bool connected = mqttClient.connect(
        Config::MQTT_CLIENT_ID,
        Config::MQTT_USERNAME,
        Config::MQTT_PASSWORD
    );

    if (connected) {
        Serial.println(" Connected!");
        mqttClient.subscribe(commandTopic, 1);
        Serial.printf("Subscribed to: %s\n", commandTopic);
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
        Serial.printf("Published to %s\n", topic);
    } else {
        Serial.printf("Failed to publish to %s\n", topic);
    }

    return result;
}

bool MQTTManager::publishResult(const char* type, const char* requestId, JsonObject payload) {
    JsonDocument doc;
    doc["kind"] = "RESULT";
    doc["type"] = type;
    doc["requestId"] = requestId;
    doc["timestamp"] = millis() / 1000;
    if (!payload.isNull()) {
        doc["payload"] = payload;
    } else {
        doc.createNestedObject("payload");
    }

    String json;
    serializeJson(doc, json);
    return publish(resultTopic, json.c_str());
}

bool MQTTManager::publishEvent(const char* type, JsonObject payload) {
    JsonDocument doc;
    doc["kind"] = "EVENT";
    doc["type"] = type;
    doc["timestamp"] = millis() / 1000;
    if (!payload.isNull()) {
        doc["payload"] = payload;
    } else {
        doc.createNestedObject("payload");
    }

    String json;
    serializeJson(doc, json);
    return publish(eventTopic, json.c_str());
}

bool MQTTManager::publishStatus(const char* status) {
    JsonDocument doc;
    doc["kind"] = "EVENT";
    doc["type"] = "STATUS";
    doc["timestamp"] = millis() / 1000;

    JsonObject payload = doc["payload"].to<JsonObject>();
    payload["status"] = status;
    payload["version"] = Config::HUB_VERSION;
    payload["uptime"] = millis() / 1000;

    String json;
    serializeJson(doc, json);
    return publish(eventTopic, json.c_str());
}

void MQTTManager::setMessageCallback(MessageCallback callback) {
    userCallback = callback;
}

void MQTTManager::mqttCallback(char* topic, uint8_t* payload, unsigned int length) {
    char message[length + 1];
    memcpy(message, payload, length);
    message[length] = '\0';

    Serial.printf("MQTT [%s]: %s\n", topic, message);

    if (userCallback) {
        userCallback(topic, message);
    }
}
