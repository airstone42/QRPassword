# QRPassword

A set of programs for transmitting sensitive information from a phone(Android) to a desktop browser(Chrome).

## Introduction

There are three programs in this repository, including an Android application, a Chrome extension, and a server-side program.

The Android application is a simple password manager with basic CRUD functions and QR code reading function mainly. Please note that this password manager is not completely secure, because it stores passwords in plain text in the SQLite database. If your Android device has been rooted, then the database file can be accessed by others. So please make sure that your Android device is not rooted when you are using these programs.

The Chrome extension is a QR code generator. After generating QR code contains the key for encryption, it polls the server for encrypted information uploaded sensitive information by Android device and decrypts it. Finally, it auto fills the form with sensitive information including username and password. Please note that this program is not available on web pages that use react, Vue.js, etc.

The server-side program behaves differently when visited in different HTTP methods. When POST by Android device, it temporarily stores encrypted sensitive information in the Redis database. When GET by Chrome extension with a query string like "/?id=", it searches and returns corresponding information in Redis when it exists. When GET in a common way, it shows a web page with a login form for testing.

## Dependencies
- Android (API Level 26)
- Chromium
- Redis
- PHP & php-redis

## Usage & Process
Make sure that desktop and Android device can access the same server, assuming it is ```192.168.0.2```.

1. Add your username and password information in Android application.

2. Click the Chrome extension button when you are on the login page. After showing the QR code contains key and initial vector for AES 128 CBC encryption algorithm, hostname, and a random ID on the pop-up web page, it starts polling the server with ID to search for encrypted information from Android device, which lasts 10 seconds.

3. Click the float action button in Android application, then scan the QR code on the pop-up web page. It searches the corresponding row in the database, then encrypts information with key and initial vector and send encrypted information to the server.

4. The server temporarily stores information from Android application. When polled by Chrome extension with ID, it returns information to the Chrome extension and deletes that row.

5. Chrome extension stops polling and decrypts information, then fill the form with it.
