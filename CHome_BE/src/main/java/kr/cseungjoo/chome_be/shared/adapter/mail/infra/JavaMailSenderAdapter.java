package kr.cseungjoo.chome_be.shared.adapter.mail.infra;

import kr.cseungjoo.chome_be.shared.port.out.MailSenderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JavaMailSenderAdapter implements MailSenderPort {

    private final JavaMailSender javaMailSender;

    @Override
    public void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    @Override
    public void sendEmailVerification(String to, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[CHome] 이메일 인증");
        message.setText("아래 링크를 클릭하여 이메일을 인증해주세요.\n\n" + verificationLink);
        javaMailSender.send(message);
    }
}