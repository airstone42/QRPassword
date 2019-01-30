function encrypt(secretKey, initVector, plain) {
    var key = CryptoJS.enc.Utf8.parse(secretKey);
    var iv = CryptoJS.enc.Utf8.parse(initVector);
    var encrypted = CryptoJS.AES.encrypt(plain, key, {iv: iv});
    return encrypted.toString();
}

function decrypt(secretKey, initVector, encrypted) {
    var key = CryptoJS.enc.Utf8.parse(secretKey);
    var iv = CryptoJS.enc.Utf8.parse(initVector);
    var plain = CryptoJS.AES.decrypt(encrypted, key, {iv: iv});
    return plain.toString(CryptoJS.enc.Utf8);
}
