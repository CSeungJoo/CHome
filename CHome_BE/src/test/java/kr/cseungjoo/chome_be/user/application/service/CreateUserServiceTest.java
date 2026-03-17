package kr.cseungjoo.chome_be.user.application.service;

import kr.cseungjoo.chome_be.shared.port.out.MailSenderPort;
import kr.cseungjoo.chome_be.user.application.exception.AlreadyExistsUserException;
import kr.cseungjoo.chome_be.user.application.port.out.EmailVerificationTokenPort;
import kr.cseungjoo.chome_be.user.application.port.out.UserRepositoryPort;
import kr.cseungjoo.chome_be.user.domain.User;
import kr.cseungjoo.chome_be.user.port.in.CreateUserCommand;
import kr.cseungjoo.chome_be.user.port.in.CreateUserResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @InjectMocks
    private CreateUserService createUserService;

    @Mock
    private PasswordEncoder pwdEncoder;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private EmailVerificationTokenPort emailVerificationTokenPort;

    @Mock
    private MailSenderPort mailSenderPort;

    @Test
    @DisplayName("유저 생성에 성공한다")
    void success() {
        // given
        CreateUserCommand command = new CreateUserCommand("홍길동", "test@example.com", "password123");

        given(userRepositoryPort.exists("test@example.com")).willReturn(false);
        given(pwdEncoder.encode("password123")).willReturn("encoded-pw");
        given(userRepositoryPort.save(any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return User.restore(1L, user.getName(), user.getEmail(), user.getPassword(),
                    user.getRole(), user.getEmailVerifyAt(), user.getCreatedAt(), user.getLastLogin());
        });
        given(emailVerificationTokenPort.issue(1L)).willReturn("verify-token");

        // when
        CreateUserResult result = createUserService.execute(command);

        // then
        assertThat(result.name()).isEqualTo("홍길동");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.EmailVerified()).isFalse();
        assertThat(result.createdAt()).isNotNull();

        then(mailSenderPort).should().sendEmailVerification(eq("test@example.com"), contains("verify-token"));
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 AlreadyExistsUserException 발생")
    void failWhenEmailAlreadyExists() {
        // given
        CreateUserCommand command = new CreateUserCommand("홍길동", "test@example.com", "password123");
        given(userRepositoryPort.exists("test@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> createUserService.execute(command))
                .isInstanceOf(AlreadyExistsUserException.class);

        then(userRepositoryPort).should(never()).save(any());
    }
}
