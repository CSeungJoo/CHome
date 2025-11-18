#include <Arduino.h>
#include "LocalHub.h"

LocalHub hub;

void setup() {
    Serial.begin(115200);
    Serial.println("begin");
    hub.begin();
}

void loop() {
    Serial.println("loop");
    hub.loop();
}