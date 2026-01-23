package kr.cseungjoo.chome_be.global.security.port;

import kr.cseungjoo.chome_be.user.domain.Role;

public interface TokenProvider {
    String issue(Long userId, Role role);
    Long resolve(String token);
    Role resolveRole(String token);
    boolean validate(String token);
}
