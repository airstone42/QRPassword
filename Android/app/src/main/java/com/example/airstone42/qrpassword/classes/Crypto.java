package com.example.airstone42.qrpassword.classes;

import android.util.Base64;

public class Crypto {

    public static String encrypt(String plain) {
        return new String(Base64.encode(plain.getBytes(), Base64.NO_WRAP | Base64.URL_SAFE));
    }

    public static String decrypt(String encrypted) {
        return new String(Base64.decode(encrypted.getBytes(), Base64.DEFAULT));
    }

}