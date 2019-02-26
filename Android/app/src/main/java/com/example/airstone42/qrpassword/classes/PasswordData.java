package com.example.airstone42.qrpassword.classes;

public class PasswordData {

    private int id;
    private String website;
    private String url;
    private String username;
    private String password;

    public PasswordData(int id, String website, String url, String username, String password) {
        this.id = id;
        this.website = website;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public int getId() { return this.id; }

    public String getWebsite() {
        return this.website;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

}
