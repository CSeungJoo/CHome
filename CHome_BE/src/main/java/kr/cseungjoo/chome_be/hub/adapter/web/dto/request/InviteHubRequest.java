package kr.cseungjoo.chome_be.hub.adapter.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.cseungjoo.chome_be.hub.domain.HubAction;

import java.util.List;

public record InviteHubRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String targetEmail,

        @NotEmpty
        List<@NotNull HubAction> permissions
) {
}
