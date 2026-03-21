package kr.cseungjoo.chome_be.shared.port.out;

public interface MailSenderPort {
    void send(String to, String subject, String body);
    void sendEmailVerification(String to, String verificationLink);
}
