package org.tastefuljava.flipit.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    public static byte[] hash(String algorithm, byte[] bytes)
            throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(bytes);
        return digest.digest();
    }

    public static String hex(byte[] bytes) {
        char[] chars = new char[2*bytes.length];
        int i = 0;
        for (byte b: bytes) {
            chars[i++] = HEX[(b>>4)&0x0F];
            chars[i++] = HEX[b&0x0F];
        }
        return new String(chars);
    }
}
