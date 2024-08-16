package org.chad.cloudvault.utils;

import java.security.SecureRandom;
import java.util.Random;

public class RandomStringGenerator {

    // 字符池，包含26个大小写字母和数字
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CHAR_POOL_SIZE = CHARACTERS.length();

    // SecureRandom 是一个更强大的随机数生成器
    private static final Random RANDOM = new SecureRandom();

    /**
     * 生成指定长度的随机字符串
     *
     * @param length 要生成的字符串长度
     * @return 生成的随机字符串
     */
    public static String generateRandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }

        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // 从字符池中随机选择一个字符并附加到结果字符串中
            int index = RANDOM.nextInt(CHAR_POOL_SIZE);
            result.append(CHARACTERS.charAt(index));
        }

        return result.toString();
    }
}