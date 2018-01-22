package com.example.airstone42.qrpassword.classes;

public class Crypto {

    public final static String key = "10BE6851DD97DD518C8BC071A82ED154";

    public static String encrypt(String plainString, String keyString) {
        char[] plain = new char[512];
        char[] key = new char[512];
        char[] encrypted = new char[512];
        plain = plainString.toCharArray();
        key = keyString.toCharArray();
        encrypted = plainString.toCharArray();
        if (plain.length > key.length) {
            int cycle = 0;
            for (int count = 0; count < plain.length; count++) {
                if (cycle == key.length)
                    cycle = 0;
                encrypted[count] = (char) ((int) plain[count] ^ (int) key[cycle++]);
            }
        } else
            for (int count = 0; count < plain.length; count++)
                encrypted[count] = (char) ((int) plain[count] ^ (int) key[count]);
        String encryptedString = String.valueOf(encrypted);
        return encryptedString;
    }

    public static String decrypt(String encryptedString, String keyString) {
        char[] encrypted = new char[512];
        char[] key = new char[512];
        char[] decrypted = new char[512];
        encrypted = encryptedString.toCharArray();
        key = keyString.toCharArray();
        decrypted = encryptedString.toCharArray();
        if (encrypted.length > key.length) {
            int cycle = 0;
            for (int count = 0; count < encrypted.length; count++) {
                if (cycle == key.length)
                    cycle = 0;
                decrypted[count] = (char) ((int) encrypted[count] ^ (int) key[cycle++]);
            }
        } else
            for (int count = 0; count < encrypted.length; count++)
                decrypted[count] = (char) ((int) encrypted[count] ^ (int) key[count]);
        String decryptedString = String.valueOf(decrypted);
        return decryptedString;
    }

}