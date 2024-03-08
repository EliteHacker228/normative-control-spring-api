package ru.maeasoftoworks.normativecontrol.api.utils;

import lombok.SneakyThrows;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashingUtils {
    @SneakyThrows
    public static String sha256(String stringToHash) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(stringToHash.getBytes(StandardCharsets.UTF_8));
        BigInteger noHash = new BigInteger(1, hash);
        return noHash.toString(16);
    }
}
