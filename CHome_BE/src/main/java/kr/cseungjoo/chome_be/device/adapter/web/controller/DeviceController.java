package kr.cseungjoo.chome_be.device.adapter.web.controller;

import kr.cseungjoo.chome_be.device.adapter.web.dto.response.GetAccessibleDevicesResponse;
import kr.cseungjoo.chome_be.device.port.in.FindAccessibleDeviceCommand;
import kr.cseungjoo.chome_be.device.port.in.FindAccessibleDeviceResult;
import kr.cseungjoo.chome_be.device.port.in.FindAccessibleDeviceUseCase;
import kr.cseungjoo.chome_be.shared.adapter.web.annotation.ApiV1;
import kr.cseungjoo.chome_be.shared.adapter.web.context.AuthenticatedUser;
import kr.cseungjoo.chome_be.shared.adapter.web.response.BasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@ApiV1
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final FindAccessibleDeviceUseCase findAccessibleDeviceUseCase;

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
}
