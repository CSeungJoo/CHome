package kr.cseungjoo.chome_be.hub.adapter.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterHubRequest(
        @NotBlank(message = "시리얼 넘버는 필수입니다.")
        String serialNumber,

        String alias
) {
    public RegisterHubRequest {
        serialNumber = serialNumber.trim();

        if (alias == null) {
            alias = "hub";
        }
    }
}