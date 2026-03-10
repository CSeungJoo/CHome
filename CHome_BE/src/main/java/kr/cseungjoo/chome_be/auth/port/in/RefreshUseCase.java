package kr.cseungjoo.chome_be.auth.port.in;

public interface RefreshUseCase {
    RefreshResult execute(RefreshCommand command);
}
