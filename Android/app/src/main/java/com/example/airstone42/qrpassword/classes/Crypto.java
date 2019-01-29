package com.example.airstone42.qrpassword.classes;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    public static String encrypt(String secretKey, String initVector, String plain) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] origin = plain.getBytes(StandardCharsets.UTF_8);
            return new String(Base64.encode(cipher.doFinal(origin), Base64.NO_WRAP | Base64.URL_SAFE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] origin = cipher.doFinal(Base64.decode(encrypted.getBytes(), Base64.DEFAULT));
            return new String(origin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encode(String plain) {
        return new String(Base64.encode(plain.getBytes(), Base64.NO_WRAP | Base64.URL_SAFE));
    }

    public static String decode(String encrypted) {
        return new String(Base64.decode(encrypted.getBytes(), Base64.DEFAULT));
    }

    public static void main(String[] args) { }
}