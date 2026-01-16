package kr.cseungjoo.chome_be.user.application.service;

import kr.cseungjoo.chome_be.user.application.port.in.VerifyEmailUseCase;
import kr.cseungjoo.chome_be.user.application.port.out.EmailVerificationTokenPort;
import kr.cseungjoo.chome_be.user.application.port.out.UserRepositoryPort;
import kr.cseungjoo.chome_be.user.domain.User;
import kr.cseungjoo.chome_be.user.application.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerifyEmailService implements VerifyEmailUseCase {

    private final EmailVerificationTokenPort emailVerificationTokenPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    @Transactional
    public void execute(String token) {
        Long userId = emailVerificationTokenPort.resolve(token);

        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        user.verifyEmail();
        userRepositoryPort.save(user);
    }
}
