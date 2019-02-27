package com.example.airstone42.qrpassword.classes;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    public static class CodeContent {
        private String sessionID;
        private String secretKey;
        private String hostname;

        CodeContent(String sessionID, String secretKey, String hostname) {
            this.sessionID = String.valueOf(Integer.parseInt(sessionID, 16));
            this.secretKey = String.valueOf(Long.parseLong(secretKey, 16));
            this.hostname = new String(Base64.decode(hostname, Base64.DEFAULT));
        }

        public String getSessionID() {
            return sessionID;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public String getHostname() {
            return hostname;
        }
    }

    public static String encrypt(String secretKey, String initVector, String plain) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] origin = plain.getBytes(StandardCharsets.UTF_8);
            return new String(Base64.encode(cipher.doFinal(origin), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String secretKey, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] origin = cipher.doFinal(Base64.decode(encrypted.getBytes(), Base64.DEFAULT));
            return new String(origin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CodeContent parse(String encoded) {
        return new CodeContent(encoded.substring(0, 7), encoded.substring(7, 21), encoded.substring(21));
    }

    public static void main(String[] args) { }

}