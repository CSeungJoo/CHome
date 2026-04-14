#pragma once
#include <Arduino.h>

namespace Config {
    // Hub Identity
    static constexpr const char* HUB_VERSION       = "1.0.0";
    static constexpr const char* SERIAL_NUMBER      = "hub-001";  // DB에 등록된 시리얼 번호와 일치해야 함

    // WiFi
    static constexpr const char* WIFI_SSID          = "SK_WiFiGIGA2F88_2.4G";
    static constexpr const char* WIFI_PASSWORD      = "AGS25@3243";
    static constexpr uint32_t    WIFI_TIMEOUT_MS    = 20000;

    // MQTT Connection
    static constexpr const char* MQTT_BROKER        = "192.168.0.10";
    static constexpr uint16_t    MQTT_PORT          = 1883;
    // MQTT 인증: username = serialNumber (백엔드 ACL 규칙)
    static constexpr const char* MQTT_CLIENT_ID     = SERIAL_NUMBER;
    static constexpr const char* MQTT_USERNAME      = SERIAL_NUMBER;
    static constexpr const char* MQTT_PASSWORD      = "";

    // MQTT Topics — hub/{serialNumber}/command | result | event
    // 런타임에 생성 (LocalHub::begin에서 조립)

    // BLE
    static constexpr const char* BLE_DEVICE_NAME    = "LocalHub";
    static constexpr uint32_t    BLE_SCAN_DURATION  = 5;
    static constexpr uint16_t    BLE_SCAN_INTERVAL  = 45;
    static constexpr uint16_t    BLE_SCAN_WINDOW    = 15;

    // General
    static constexpr uint32_t    LOOP_DELAY_MS      = 10;
}
