package kr.cseungjoo.chome_be.device.adapter.web.dto.request;

public record ChangeDeviceAliasRequest(
        String alias
) {
    public ChangeDeviceAliasRequest {
        if (alias == null) {
            alias = "device";
        }
    }
}
