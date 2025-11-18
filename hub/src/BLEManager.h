#pragma once
#include <NimBLEDevice.h>
#include <map>
#include <vector>
#include "Config.h"

class BLEManager {
public:
    using DeviceDiscoveredCallback = void (*)(const char* address, const char* name);
    using DataReceivedCallback = void (*)(const char* address, const char* characteristic, const uint8_t* data, size_t length);

    BLEManager();
    void begin();
    void loop();
    void startScan();
    void stopScan();
    bool connectToDevice(const char* address);
    bool disconnectDevice(const char* address);
    bool isDeviceConnected(const char* address);
    void setDeviceDiscoveredCallback(DeviceDiscoveredCallback callback);
    void setDataReceivedCallback(DataReceivedCallback callback);
    std::vector<String> getConnectedDevices();

private:
    struct DeviceInfo {
        NimBLEClient* client;
        NimBLEAddress address;
        String name;
        uint32_t lastSeen;
        bool shouldConnect;
    };

    class ClientCallbacks : public NimBLEClientCallbacks {
    public:
        ClientCallbacks(BLEManager* manager) : manager(manager) {}
        void onConnect(NimBLEClient* pClient);
        void onDisconnect(NimBLEClient* pClient, int reason);
    private:
        BLEManager* manager;
    };

    class ScanCallbacks : public NimBLEScanCallbacks {
    public:
        ScanCallbacks(BLEManager* manager) : manager(manager) {}
        void onResult(NimBLEAdvertisedDevice* advertisedDevice);
        void onScanEnd(NimBLEScanResults results);
    private:
        BLEManager* manager;
    };

    static void notifyCallback(NimBLERemoteCharacteristic* pChar, uint8_t* data, size_t length, bool isNotify);
    bool connectToServer(const String& address);
    void handleDeviceDiscovery(NimBLEAdvertisedDevice* device);
    void processPendingConnections();

    std::map<String, DeviceInfo> devices;
    std::vector<String> pendingConnections;
    NimBLEScan* scan;
    bool isScanning;

    static DeviceDiscoveredCallback discoveryCallback;
    static DataReceivedCallback dataCallback;
    static BLEManager* instance;
};
