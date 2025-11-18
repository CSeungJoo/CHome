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

    // Internal methods
    void handleMQTTControl(const char* topic, const char* payload);
    void printStatus();

    WiFiManager wifi;
    BLEManager ble;
    MQTTManager mqtt;

    uint32_t lastStatusPrint;
    static LocalHub* instance;

    static constexpr uint32_t STATUS_PRINT_INTERVAL_MS = 60000;
};
