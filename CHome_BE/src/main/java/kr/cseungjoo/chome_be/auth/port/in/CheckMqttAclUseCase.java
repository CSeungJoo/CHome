package kr.cseungjoo.chome_be.auth.port.in;

public interface CheckMqttAclUseCase {
    void execute(CheckMqttAclCommand command);
}
