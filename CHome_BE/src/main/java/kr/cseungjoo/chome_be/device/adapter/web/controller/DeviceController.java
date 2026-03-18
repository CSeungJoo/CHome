package kr.cseungjoo.chome_be.device.adapter.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.cseungjoo.chome_be.device.adapter.web.dto.request.ChangeDeviceAliasRequest;
import kr.cseungjoo.chome_be.device.adapter.web.dto.response.ChangeDeviceAliasResponse;
import kr.cseungjoo.chome_be.device.adapter.web.dto.response.GetAccessibleDevicesResponse;
import kr.cseungjoo.chome_be.device.port.in.ChangeDeviceAliasCommand;
import kr.cseungjoo.chome_be.device.port.in.ChangeDeviceAliasResult;
import kr.cseungjoo.chome_be.device.port.in.ChangeDeviceAliasUseCase;
import kr.cseungjoo.chome_be.device.port.in.FindAccessibleDeviceCommand;
import kr.cseungjoo.chome_be.device.port.in.FindAccessibleDeviceResult;
import kr.cseungjoo.chome_be.device.port.in.FindAccessibleDeviceUseCase;
import kr.cseungjoo.chome_be.shared.adapter.web.annotation.ApiV1;
import kr.cseungjoo.chome_be.shared.adapter.web.context.AuthenticatedUser;
import kr.cseungjoo.chome_be.shared.adapter.web.response.BasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@ApiV1
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final FindAccessibleDeviceUseCase findAccessibleDeviceUseCase;
    private final ChangeDeviceAliasUseCase changeDeviceAliasUseCase;

    @GetMapping
    public ResponseEntity<BasicResponse.BaseResponse> getAccessibleDevices(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam long hubId
            ) {
        FindAccessibleDeviceResult result = findAccessibleDeviceUseCase.execute(
                new FindAccessibleDeviceCommand(
                        authenticatedUser.userId(),
                        hubId
                )
        );

        GetAccessibleDevicesResponse response = new GetAccessibleDevicesResponse(
                result.devices().stream().map(d ->
                        new GetAccessibleDevicesResponse.AccessibleDevice(
                                d.id(),
                                d.serialNumber(),
                                d.name(),
                                d.type(),
                                d.alias()
                        )
                ).toList()
        );

        return BasicResponse.ok(response);
    }

    @PutMapping("/{deviceId}")
    public ResponseEntity<BasicResponse.BaseResponse> changeDeviceAlias(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long deviceId,
            @Valid @RequestBody @NotNull ChangeDeviceAliasRequest request
    ) {

        ChangeDeviceAliasResult result = changeDeviceAliasUseCase.execute(
                new ChangeDeviceAliasCommand(
                        deviceId,
                        request.alias(),
                        authenticatedUser.userId()
                )
        );

        ChangeDeviceAliasResponse changeDeviceAliasResponse = new ChangeDeviceAliasResponse(
                result.alias(),
                result.changedAt()
        );

        return BasicResponse.ok(changeDeviceAliasResponse);
    }
}
