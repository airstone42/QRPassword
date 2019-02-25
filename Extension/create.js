const serverURL = 'http://192.168.0.2:3000'

const code = new QRCode(document.getElementById('code'), {
    width: 300,
    height: 300,
    useSVG: true,
    correctLevel: QRCode.CorrectLevel.H
})

const sessionID = rand(8)
const secretKey = rand(16)
const initVector = '0000000000000000'

function rand(n) {
    let rand = ''
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
            hostname: url.hostname
        }
        code.makeCode(encode(codeContent))
        let poll = 0
        let getInfo = () => {
            let xhr = new XMLHttpRequest()
            xhr.open('GET', serverURL + '/?id=' + codeContent.id)
            xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded')
            xhr.send()
            xhr.onreadystatechange = () => {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    if (xhr.responseText) {
                        const infoJSON = JSON.parse(xhr.responseText)
                        if (codeContent.hostname === decrypt(secretKey, initVector, infoJSON.hostname)) {
                            clearTimeout(poll)
                            flag = true
                            chrome.tabs.query({
                                active: true,
                                currentWindow: true
                            }, tabs => {
                                chrome.tabs.sendMessage(tabs[0].id, {
                                    username: decrypt(secretKey, initVector, infoJSON.username),
                                    password: decrypt(secretKey, initVector, infoJSON.password)
                                }, function(response) {
                                    console.log(response.success)
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
                poll = setTimeout(getInfo, 500)
            } else if (count > 20) {
                clearTimeout(poll)
                document.getElementById('code').innerHTML = '<h1>Timeout!</h1>' + '<p>Please click again.</p>'
            }
        }
        getInfo()
    })
}

createCode()
