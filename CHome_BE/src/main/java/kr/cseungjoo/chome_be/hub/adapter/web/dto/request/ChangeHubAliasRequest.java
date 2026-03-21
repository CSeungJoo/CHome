package kr.cseungjoo.chome_be.hub.adapter.web.dto.request;

public record ChangeHubAliasRequest(
        String alias
) {
    public ChangeHubAliasRequest {
        if (alias == null) {
            alias = "hub";
        }
    }
}
