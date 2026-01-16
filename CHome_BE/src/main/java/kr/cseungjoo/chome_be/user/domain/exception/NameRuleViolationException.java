package kr.cseungjoo.chome_be.user.domain.exception;

import kr.cseungjoo.chome_be.global.exception.DomainException;

public class NameRuleViolationException extends DomainException implements UserException {
    public NameRuleViolationException(String s) {
        super(s);
    }
}
