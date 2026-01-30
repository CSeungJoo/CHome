package kr.cseungjoo.chome_be.hub.presentation.dto.response;

import java.time.Instant;

public record RegisterHubResponse(String serialNumber, String alias, Instant createdAt) {}