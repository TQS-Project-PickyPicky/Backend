package tqs.project.backend.util;

import java.security.SecureRandom;

public class TokenUtils {
    private static final SecureRandom random = new SecureRandom();

    public static Integer generateParcelToken() {
        return random.nextInt(1000000);
    }
}
