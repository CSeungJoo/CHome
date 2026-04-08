#pragma once
#include <PubSubClient.h>
#include <WiFi.h>
#include <ArduinoJson.h>
#include "Config.h"

class MQTTManager {
public:
    using MessageCallback = void (*)(const char* topic, const char* payload);

    MQTTManager();
    bool begin(const char* broker, uint16_t port, const char* serialNumber);
    void loop();
    bool isConnected();
    bool publish(const char* topic, const char* payload, bool retained = false);
    bool publishResult(const char* type, const char* requestId, JsonObject payload);
    bool publishEvent(const char* type, JsonObject payload);
    bool publishStatus(const char* status);
    void setMessageCallback(MessageCallback callback);

    const char* getCommandTopic() const { return commandTopic; }
    const char* getResultTopic() const { return resultTopic; }
    const char* getEventTopic() const { return eventTopic; }

private:
    bool connect();
    static void mqttCallback(char* topic, uint8_t* payload, unsigned int length);

    WiFiClient wifiClient;
    PubSubClient mqttClient;
    const char* broker;
    uint16_t port;
    uint32_t lastReconnectAttempt;
    static MessageCallback userCallback;
    static constexpr uint32_t RECONNECT_INTERVAL_MS = 5000;

    // hub/{serialNumber}/command | result | event
    char commandTopic[64];
    char resultTopic[64];
    char eventTopic[64];
    const char* serialNumber;
};
