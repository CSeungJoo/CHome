package kr.cseungjoo.chome_be.user.adapter.infra.token;

import kr.cseungjoo.chome_be.user.application.port.out.EmailVerificationTokenPort;
import kr.cseungjoo.chome_be.user.adapter.infra.token.exception.InvalidTokenException;
import kr.cseungjoo.chome_be.user.adapter.infra.token.exception.TokenBuildFailureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class AesEmailVerificationTokenAdapter implements EmailVerificationTokenPort {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;

    private final SecretKey secretKey;

    public AesEmailVerificationTokenAdapter(@Value("${app.email-token.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = new SecretKeySpec(Arrays.copyOf(keyBytes, 32), "AES");
    }

    @Override
    public String issue(Long userId) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            byte[] encrypted = cipher.doFinal(String.valueOf(userId).getBytes(StandardCharsets.UTF_8));

            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
        } catch (Exception e) {
            throw new TokenBuildFailureException("토큰 생성 실패", e);
        }
    }

    @Override
    public Long resolve(String token) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(token);

            byte[] iv = Arrays.copyOfRange(decoded, 0, GCM_IV_LENGTH);
            byte[] encrypted = Arrays.copyOfRange(decoded, GCM_IV_LENGTH, decoded.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            String decrypted = new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
            return Long.parseLong(decrypted);
        } catch (Exception e) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.", e);
        }
    }
}