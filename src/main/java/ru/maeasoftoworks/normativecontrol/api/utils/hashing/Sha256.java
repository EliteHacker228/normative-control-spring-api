package ru.maeasoftoworks.normativecontrol.api.utils.hashing;

import lombok.SneakyThrows;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static java.awt.SystemColor.text;

public class Sha256 {

    @SneakyThrows
    public static String getStringSha256(String input) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.US_ASCII));
        return String.format("%064x", new BigInteger(1, hash));
    }
}
