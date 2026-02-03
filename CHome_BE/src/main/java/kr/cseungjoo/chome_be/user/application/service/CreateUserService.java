package kr.cseungjoo.chome_be.user.application.service;

import kr.cseungjoo.chome_be.common.port.out.MailSenderPort;
import kr.cseungjoo.chome_be.user.application.command.CreateUserCommand;
import kr.cseungjoo.chome_be.user.application.exception.AlreadyExistsUserException;
import kr.cseungjoo.chome_be.user.application.port.in.CreateUserUseCase;
import kr.cseungjoo.chome_be.user.application.port.out.EmailVerificationTokenPort;
import kr.cseungjoo.chome_be.user.application.port.out.UserRepositoryPort;
import kr.cseungjoo.chome_be.user.application.result.CreateUserResult;
import kr.cseungjoo.chome_be.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final PasswordEncoder pwdEncoder;
    private final UserRepositoryPort userRepositoryPort;
    private final EmailVerificationTokenPort emailVerificationTokenPort;
    private final MailSenderPort mailSenderPort;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    @Override
    @Transactional
    public CreateUserResult execute(CreateUserCommand command) {
        if (userRepositoryPort.exists(command.email())) {
            throw new AlreadyExistsUserException("이미 존재하는 유저의 이메일 입니다.");
        }

        String encodedPassword = pwdEncoder.encode(command.password());

        User user = User.create(command.name(), command.email(), encodedPassword);
        User saved = userRepositoryPort.save(user);

        String token = emailVerificationTokenPort.issue(saved.getId());
        String verifyLink = baseUrl + "/users/verify?token=" + token;

        mailSenderPort.sendEmailVerification(saved.getEmail(), verifyLink);

        return new CreateUserResult(
                saved.getName(),
                saved.getEmail(),
                saved.isEmailVerified(),
                saved.getCreatedAt(),
                saved.getLastLogin()
        );
    }
}
