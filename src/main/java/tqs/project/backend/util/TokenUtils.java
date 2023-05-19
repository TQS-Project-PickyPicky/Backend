package tqs.project.backend.util;

import java.util.Random;

public class TokenUtils {
    private static final Random random = new Random();

    public static Integer generateParcelToken() {
        return random.nextInt(1000000);
    }
}
