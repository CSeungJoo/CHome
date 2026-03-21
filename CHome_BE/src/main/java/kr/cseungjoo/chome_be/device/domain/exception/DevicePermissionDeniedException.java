package kr.cseungjoo.chome_be.device.domain.exception;

public class DevicePermissionDeniedException extends RuntimeException implements DeviceException {
    public DevicePermissionDeniedException(String message) {
        super(message);
    }
}
