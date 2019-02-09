const serverURL = "http://192.168.0.2"

const code = new QRCode(document.getElementById('code'), {
    width: 300,
    height: 300,
    useSVG: true,
    correctLevel: QRCode.CorrectLevel.H
})

const sessionID = rand(16)
const secretKey = rand(16)
const initVector = rand(16)

function rand(n) {
    let rand = ""
    for(let i = 0; i < n; i++)
        rand += Math.floor(Math.random() * 10)
    return rand
}

function createCode() {
    let count = 0
    let flag = false
    chrome.tabs.query({active: true}, tab => {
        const url = new URL(tab[0].url)
        const codeContent = {
            id: sessionID,
            skey: secretKey,
            iv: initVector,
            hostname: encrypt(secretKey, initVector, url.hostname)
        }
        let xhr = new XMLHttpRequest()
        xhr.open("POST", serverURL + "/extension/qrcode.php")
        xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded')
        xhr.send(btoa(JSON.stringify({
          id: codeContent.id,
          hostname: codeContent.hostname
        })))
        code.makeCode(btoa(JSON.stringify(codeContent)))
        let ID = 0
        let getInfo = () => {
            let xhr = new XMLHttpRequest()
            xhr.open("GET", serverURL + "/extension/info.php")
            xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded')
            xhr.send()
            xhr.onreadystatechange = () => {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    if (xhr.responseText) {
                        const infoJSON = JSON.parse(xhr.responseText)
                        if (infoJSON.id === sessionID) {
                            clearTimeout(ID)
                            flag = true
                            chrome.tabs.query({
                                active: true,
                                currentWindow: true
                            }, tabs => {
                                chrome.tabs.sendMessage(tabs[0].id, {
                                    username: decrypt(codeContent.skey, codeContent.iv, infoJSON.username),
                                    password: decrypt(codeContent.skey, codeContent.iv, infoJSON.password)
                                }, function(response) {
                                    console.log(response.success)
                                    /** Clear webpage info **/
                                    chrome.tabs.query({active: true}, tab => {
                                        let xhr = new XMLHttpRequest()
                                        xhr.open("POST", serverURL + "/extension/info.php")
                                        xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded')
                                        xhr.send("")
                                    })
                                    /** Clear user info **/
                                    chrome.tabs.query({active: true}, tab => {
                                        let xhr = new XMLHttpRequest()
                                        xhr.open("POST", serverURL + "/extension/qrcode.php")
                                        xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded')
                                        xhr.send("")
                                    })
                                })
                            })
                        } else
                            flag = false
                    } else
                        flag = false
                } else
                    flag = false
            }
            if (!flag && count <= 20) {
                count++
                ID = setTimeout(getInfo, 500)
            } else if (count > 20) {
                clearTimeout(ID)
                document.getElementById("code").innerHTML = "<h1>Timeout!</h1>" + "<p>Please click again.</p>"
            }
        }
        getInfo()
    })
}

createCode()
