package kr.cseungjoo.chome_be.common.port.out;

import kr.cseungjoo.chome_be.user.domain.Role;

public interface TokenProviderPort {
    String issue(Long userId, Role role);
    Long resolve(String token);
    Role resolveRole(String token);
    boolean validate(String token);
}
