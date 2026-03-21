package kr.cseungjoo.chome_be.hub.port.in;

public record ChangeHubAliasCommand(
        Long hubId,
        String alias,
        Long userId
) {
}
