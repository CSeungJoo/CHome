package kr.cseungjoo.chome_be.user.application.port.out;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface EmailVerificationTokenPort {
    String issue(Long userId);
    Long resolve(String token);
}