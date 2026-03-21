package kr.cseungjoo.chome_be.hub.adapter.web.dto.response;

import java.time.Instant;

public record DeleteHubResponse(String serialNumber, Instant deletedAt) {
}
