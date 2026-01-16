package kr.cseungjoo.chome_be.global.mail.port;

public interface MailSender {
    void send(String to, String subject, String body);
    void sendEmailVerification(String to, String verificationLink);
}
