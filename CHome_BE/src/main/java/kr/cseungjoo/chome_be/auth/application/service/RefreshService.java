package kr.cseungjoo.chome_be.auth.application.service;

import kr.cseungjoo.chome_be.auth.port.in.RefreshCommand;
import kr.cseungjoo.chome_be.auth.application.exception.InvalidRefreshTokenException;
import kr.cseungjoo.chome_be.auth.port.in.RefreshUseCase;
import kr.cseungjoo.chome_be.auth.port.in.RefreshResult;
import kr.cseungjoo.chome_be.shared.port.out.RefreshTokenPort;
import kr.cseungjoo.chome_be.shared.port.out.TokenProviderPort;
import kr.cseungjoo.chome_be.user.application.exception.UserNotFoundException;
import kr.cseungjoo.chome_be.user.port.out.UserRepositoryPort;
import kr.cseungjoo.chome_be.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshService implements RefreshUseCase {

    private final RefreshTokenPort refreshTokenPort;
    private final TokenProviderPort tokenProviderPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public RefreshResult execute(RefreshCommand command) {
        Long userId = refreshTokenPort.resolve(command.refreshToken())
                .orElseThrow(InvalidRefreshTokenException::new);

        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 기존 refresh token 폐기 (Rotation)
        refreshTokenPort.revoke(command.refreshToken());

        // 새 토큰 쌍 발급
        String newAccessToken = tokenProviderPort.issue(userId, user.getRole());
        String newRefreshToken = refreshTokenPort.issue(userId);

        return new RefreshResult(newAccessToken, newRefreshToken);
    }
}
