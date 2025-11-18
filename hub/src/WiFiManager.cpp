#include "WiFiManager.h"
#include <Arduino.h>

WiFiManager::WiFiManager() : lastReconnectAttempt(0) {}

bool WiFiManager::begin() {
    Serial.println("Initializing WiFi...");
    WiFi.mode(WIFI_STA);
    WiFi.disconnect();
    delay(100);

    return connect();
}

bool WiFiManager::connect() {
    Serial.printf("Connecting to WiFi: %s\n", Config::WIFI_SSID);

    WiFi.begin(Config::WIFI_SSID, Config::WIFI_PASSWORD);

    uint32_t startTime = millis();
    while (WiFi.status() != WL_CONNECTED) {
        if (millis() - startTime > Config::WIFI_TIMEOUT_MS) {
            Serial.println("WiFi connection timeout!");
            return false;
        }
        delay(500);
        Serial.print(".");
    }

    Serial.println();
    Serial.printf("WiFi connected! IP: %s\n", WiFi.localIP().toString().c_str());
    Serial.printf("RSSI: %d dBm\n", WiFi.RSSI());

    return true;
}

bool WiFiManager::isConnected() {
    return WiFi.status() == WL_CONNECTED;
}

void WiFiManager::reconnect() {
    uint32_t now = millis();

    if (!isConnected() && (now - lastReconnectAttempt > RECONNECT_INTERVAL_MS)) {
        Serial.println("WiFi disconnected. Attempting to reconnect...");
        lastReconnectAttempt = now;
        connect();
    }
}

String WiFiManager::getLocalIP() {
    return WiFi.localIP().toString();
}

int WiFiManager::getRSSI() {
    return WiFi.RSSI();
}
