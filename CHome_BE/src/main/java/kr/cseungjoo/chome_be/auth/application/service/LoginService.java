package kr.cseungjoo.chome_be.auth.application.service;

import kr.cseungjoo.chome_be.auth.port.in.LoginCommand;
import kr.cseungjoo.chome_be.auth.application.exception.AuthenticationFailedException;
import kr.cseungjoo.chome_be.auth.application.exception.EmailNotVerifiedException;
import kr.cseungjoo.chome_be.auth.port.in.LoginUseCase;
import kr.cseungjoo.chome_be.auth.port.in.LoginResult;
import kr.cseungjoo.chome_be.shared.port.out.RefreshTokenPort;
import kr.cseungjoo.chome_be.shared.port.out.TokenProviderPort;
import kr.cseungjoo.chome_be.user.application.port.out.UserRepositoryPort;
import kr.cseungjoo.chome_be.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final TokenProviderPort tokenProviderPort;
    private final RefreshTokenPort refreshTokenPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public LoginResult execute(LoginCommand command) {
        User user = userRepositoryPort.findByEmail(command.email())
                .orElseThrow(AuthenticationFailedException::new);

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new AuthenticationFailedException();
        }

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException();
        }

        String accessToken = tokenProviderPort.issue(user.getId(), user.getRole());
        String refreshToken = refreshTokenPort.issue(user.getId());

        return new LoginResult(accessToken, refreshToken, user.getId());
    }
}
