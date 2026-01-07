package kr.cseungjoo.chome_be.user.domain.exception;

public class NameRuleViolationException extends IllegalArgumentException {
    public NameRuleViolationException(String s) {
        super(s);
    }
}
