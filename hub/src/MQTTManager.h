#pragma once
#include <PubSubClient.h>
#include <WiFi.h>
#include <ArduinoJson.h>
#include "Config.h"

class MQTTManager {
public:
    using MessageCallback = void (*)(const char* topic, const char* payload);

    MQTTManager();
    bool begin(const char* broker, uint16_t port);
    void loop();
    bool isConnected();
    bool publish(const char* topic, const char* payload, bool retained = false);
    bool publishBLEDiscovery(const char* deviceAddress, const char* deviceName);
    bool publishBLEData(const char* deviceAddress, const char* characteristic, const uint8_t* data, size_t length);
    bool publishStatus(const char* status);
    void setMessageCallback(MessageCallback callback);

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
};
