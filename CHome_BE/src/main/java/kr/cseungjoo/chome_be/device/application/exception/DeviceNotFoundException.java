package kr.cseungjoo.chome_be.device.application.exception;

import kr.cseungjoo.chome_be.device.domain.exception.DeviceException;

public class DeviceNotFoundException extends RuntimeException implements DeviceException {
    public DeviceNotFoundException(String message) {
        super(message);
    }
}
