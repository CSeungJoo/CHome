#pragma once
#include "BLEManager.h"
#include "MQTTManager.h"
#include "WiFiManager.h"
#include "Config.h"

class LocalHub {
public:
    LocalHub();
    void begin();
    void loop();

private:
    // Callback handlers
    static void onDeviceDiscovered(const char* address, const char* name);
    static void onBLEDataReceived(const char* address, const char* characteristic, const uint8_t* data, size_t length);
    static void onMQTTMessage(const char* topic, const char* payload);

    // Command handlers — 백엔드 HubMessage 포맷 처리
    void handleCommand(const char* payload);
    void handleBLEScan(const char* requestId);
    void handleBLEConnect(const char* requestId, JsonObject& cmdPayload);
    void handleBLEDisconnect(const char* requestId, JsonObject& cmdPayload);
    void handleStatusRequest(const char* requestId);

    void printStatus();

    WiFiManager wifi;
    BLEManager ble;
    MQTTManager mqtt;

    uint32_t lastStatusPrint;
    static LocalHub* instance;

    static constexpr uint32_t STATUS_PRINT_INTERVAL_MS = 60000;
};
