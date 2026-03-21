package kr.cseungjoo.chome_be.hub.adapter.web.dto.response;

import java.time.Instant;

public record RegisterHubResponse(String serialNumber, String alias, Instant createdAt) {}