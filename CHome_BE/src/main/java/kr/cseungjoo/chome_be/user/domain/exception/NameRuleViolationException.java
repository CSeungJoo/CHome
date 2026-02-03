package kr.cseungjoo.chome_be.user.domain.exception;

public class NameRuleViolationException extends RuntimeException implements UserException {
    public NameRuleViolationException(String s) {
        super(s);
    }
}
