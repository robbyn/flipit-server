package org.tastefuljava.flipit.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
    private static final char[] HEX = "0123456789abcdef".toCharArray();
    private static final String DATE_FORMAT = "dd-MM-yyyy";

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

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static Date parseDate(String s) throws ParseException {
        if (s == null) {
            return null;
        }
        DateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
        return fmt.parse(s);
    }
}
