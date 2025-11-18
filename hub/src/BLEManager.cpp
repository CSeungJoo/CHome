#include "BLEManager.h"
#include <Arduino.h>

BLEManager* BLEManager::instance = nullptr;
BLEManager::DeviceDiscoveredCallback BLEManager::discoveryCallback = nullptr;
BLEManager::DataReceivedCallback BLEManager::dataCallback = nullptr;

BLEManager::BLEManager() : scan(nullptr), isScanning(false) {
    instance = this;
}

void BLEManager::begin() {
    Serial.println("Initializing BLE Manager...");

    NimBLEDevice::init(Config::BLE_DEVICE_NAME);
    NimBLEDevice::setPower(ESP_PWR_LVL_P9);

    scan = NimBLEDevice::getScan();
    scan->setScanCallbacks(new ScanCallbacks(this), false);
    scan->setInterval(Config::BLE_SCAN_INTERVAL);
    scan->setWindow(Config::BLE_SCAN_WINDOW);
    scan->setActiveScan(true);
    scan->setDuplicateFilter(false);

    startScan();
}

void BLEManager::startScan() {
    if (!isScanning) {
        Serial.println("Starting BLE scan...");
        scan->start(0, false);  // Continuous scan
        isScanning = true;
    }
}

void BLEManager::stopScan() {
    if (isScanning) {
        Serial.println("Stopping BLE scan...");
        scan->stop();
        isScanning = false;
    }
}

void BLEManager::loop() {
    processPendingConnections();

    // Clean up disconnected devices
    for (auto it = devices.begin(); it != devices.end();) {
        if (it->second.client && !it->second.client->isConnected()) {
            Serial.printf("Cleaning up disconnected device: %s\n", it->first.c_str());
            NimBLEDevice::deleteClient(it->second.client);
            it = devices.erase(it);
        } else {
            ++it;
        }
    }
}

void BLEManager::handleDeviceDiscovery(NimBLEAdvertisedDevice* device) {
    String address = device->getAddress().toString().c_str();
    String name = device->haveName() ? device->getName().c_str() : "Unknown";

    // Check if device already exists
    if (devices.find(address) == devices.end()) {
        DeviceInfo info;
        info.client = nullptr;
        info.address = device->getAddress();
        info.name = name;
        info.lastSeen = millis();
        info.shouldConnect = false;

        devices[address] = info;

        Serial.printf("Discovered new device: %s (%s)\n", name.c_str(), address.c_str());

        // Notify callback
        if (discoveryCallback) {
            discoveryCallback(address.c_str(), name.c_str());
        }
    } else {
        devices[address].lastSeen = millis();
    }
}

bool BLEManager::connectToDevice(const char* address) {
    String addr(address);

    if (devices.find(addr) == devices.end()) {
        Serial.printf("Device %s not found in discovered devices\n", address);
        return false;
    }

    if (devices[addr].client && devices[addr].client->isConnected()) {
        Serial.printf("Device %s already connected\n", address);
        return true;
    }

    // Add to pending connections
    pendingConnections.push_back(addr);
    devices[addr].shouldConnect = true;

    return true;
}

bool BLEManager::disconnectDevice(const char* address) {
    String addr(address);

    if (devices.find(addr) == devices.end()) {
        return false;
    }

    DeviceInfo& info = devices[addr];
    if (info.client && info.client->isConnected()) {
        info.client->disconnect();
        return true;
    }

    return false;
}

bool BLEManager::isDeviceConnected(const char* address) {
    String addr(address);

    if (devices.find(addr) == devices.end()) {
        return false;
    }

    return devices[addr].client && devices[addr].client->isConnected();
}

void BLEManager::processPendingConnections() {
    if (pendingConnections.empty()) return;

    String address = pendingConnections.front();
    pendingConnections.erase(pendingConnections.begin());

    connectToServer(address);
}

bool BLEManager::connectToServer(const String& address) {
    if (devices.find(address) == devices.end()) {
        Serial.printf("Device %s not found\n", address.c_str());
        return false;
    }

    DeviceInfo& info = devices[address];

    Serial.printf("Connecting to device: %s (%s)\n", info.name.c_str(), address.c_str());

    NimBLEClient* client = NimBLEDevice::createClient();
    client->setClientCallbacks(new ClientCallbacks(this), false);
    client->setConnectionParams(12, 12, 0, 51);
    client->setConnectTimeout(5);

    if (!client->connect(info.address)) {
        Serial.printf("Failed to connect to %s\n", address.c_str());
        NimBLEDevice::deleteClient(client);
        return false;
    }

    Serial.printf("Connected to: %s\n", address.c_str());
    info.client = client;

    // Get all services
    const std::vector<NimBLERemoteService*>& services = client->getServices(true);
    for (NimBLERemoteService* pService : services) {
        if (!pService) continue;

        // Get all characteristics for this service
        const std::vector<NimBLERemoteCharacteristic*>& chars = pService->getCharacteristics(true);
        for (NimBLERemoteCharacteristic* pChar : chars) {
            if (!pChar) continue;

            // Subscribe to notify-capable characteristics
            if (pChar->canNotify()) {
                if (pChar->subscribe(true, notifyCallback)) {
                    Serial.printf("Subscribed to characteristic: %s\n",
                                  pChar->getUUID().toString().c_str());
                }
            }
        }
    }

    return true;
}

std::vector<String> BLEManager::getConnectedDevices() {
    std::vector<String> connected;
    for (const auto& pair : devices) {
        if (pair.second.client && pair.second.client->isConnected()) {
            connected.push_back(pair.first);
        }
    }
    return connected;
}

void BLEManager::setDeviceDiscoveredCallback(DeviceDiscoveredCallback callback) {
    discoveryCallback = callback;
}

void BLEManager::setDataReceivedCallback(DataReceivedCallback callback) {
    dataCallback = callback;
}

// ====== Scan Callbacks ======
void BLEManager::ScanCallbacks::onResult(NimBLEAdvertisedDevice* advertisedDevice) {
    if (manager) {
        manager->handleDeviceDiscovery(advertisedDevice);
    }
}

void BLEManager::ScanCallbacks::onScanEnd(NimBLEScanResults results) {
    Serial.printf("Scan ended. Found %d devices\n", results.getCount());
}

// ====== Client Callbacks ======
void BLEManager::ClientCallbacks::onConnect(NimBLEClient* pClient) {
    Serial.printf("Connected to BLE device: %s\n", pClient->getPeerAddress().toString().c_str());
    pClient->updateConnParams(120, 120, 0, 60);
}

void BLEManager::ClientCallbacks::onDisconnect(NimBLEClient* pClient, int reason) {
    Serial.printf("Disconnected from %s. Reason: %d\n",
                 pClient->getPeerAddress().toString().c_str(), reason);
}

// ====== Notify Callback ======
void BLEManager::notifyCallback(NimBLERemoteCharacteristic* pChar, uint8_t* data, size_t length, bool isNotify) {
    String address = pChar->getRemoteService()->getClient()->getPeerAddress().toString().c_str();
    String uuid = pChar->getUUID().toString().c_str();

    Serial.printf("Notify from %s [%s]: ", address.c_str(), uuid.c_str());
    for (size_t i = 0; i < length; i++) {
        Serial.printf("%02X ", data[i]);
    }
    Serial.println();

    // Notify callback
    if (dataCallback) {
        dataCallback(address.c_str(), uuid.c_str(), data, length);
    }
}
