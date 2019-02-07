function encrypt(secretKey, initVector, plain) {
    const key = CryptoJS.enc.Utf8.parse(secretKey)
    const iv = CryptoJS.enc.Utf8.parse(initVector)
    const encrypted = CryptoJS.AES.encrypt(plain, key, {iv: iv})
    return encrypted.toString()
}

function decrypt(secretKey, initVector, encrypted) {
    const key = CryptoJS.enc.Utf8.parse(secretKey)
    const iv = CryptoJS.enc.Utf8.parse(initVector)
    const plain = CryptoJS.AES.decrypt(encrypted, key, {iv: iv})
    return plain.toString(CryptoJS.enc.Utf8)
}
