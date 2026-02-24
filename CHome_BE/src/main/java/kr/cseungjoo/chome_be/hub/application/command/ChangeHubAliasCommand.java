package kr.cseungjoo.chome_be.hub.application.command;

public record ChangeHubAliasCommand(
        Long hubId,
        String alias,
        Long userId
) {
}
