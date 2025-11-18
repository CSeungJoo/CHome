#pragma once
#include <WiFi.h>
#include "Config.h"

class WiFiManager {
public:
    WiFiManager();
    bool begin();
    bool isConnected();
    void reconnect();
    String getLocalIP();
    int getRSSI();

private:
    bool connect();
    uint32_t lastReconnectAttempt;
    static constexpr uint32_t RECONNECT_INTERVAL_MS = 30000;
};
