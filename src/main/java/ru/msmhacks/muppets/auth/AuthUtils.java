package ru.msmhacks.muppets.auth;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthUtils {
    public static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5Hash = number.toString(16);

            while (md5Hash.length() < 32) {
                md5Hash = "0" + md5Hash;
            }

            return md5Hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSha256Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String sha256Hash = number.toString(16);

            while (sha256Hash.length() < 64) {
                sha256Hash = "0" + sha256Hash;
            }

            return sha256Hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static int stringCharSum(String input) {
        int out = 0;
        for (byte c : input.getBytes()) {
            out += c;
        }
        return out;
    }

}
