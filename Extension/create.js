function rand(n) {
    var rand = "";
    for(var i = 0; i < n; i++)
        rand += Math.floor(Math.random() * 10);
    return rand;
}

function createCode() {
    var info = "";
    var server = "http://192.168.0.2";
    chrome.tabs.getSelected(null, function(tab) {
        var url = new URL(tab.url);
        var codeContent = {
            "randkey": randomKey,
            "skey": secretKey,
            "iv": initVector,
            "hostname": encrypt(secretKey, initVector, url.hostname)
        };
        var codeText = btoa(JSON.stringify(codeContent));
        var xhr = new XMLHttpRequest();
        xhr.open("POST", server + "/extension/qrcode.php");
        xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded');
        xhr.send(codeText);
        code.makeCode(codeText);
        var ID = 0;
        var getInfo = function() {
            var xhr = new XMLHttpRequest();
            xhr.open("GET", server + "/extension/info.php");
            xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded');
            xhr.send();
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    if (xhr.responseText != "") {
                        var infoJSON = JSON.parse(xhr.responseText);
                        if (infoJSON.randkey == randomKey) {
                            clearTimeout(ID);
                            chrome.tabs.query({
                                active: true,
                                currentWindow: true
                            }, function(tabs) {
                                chrome.tabs.sendMessage(tabs[0].id, {
                                    username: decrypt(infoJSON.skey, infoJSON.iv, infoJSON.username),
                                    password: decrypt(infoJSON.skey, infoJSON.iv, infoJSON.password)
                                }, function(response) {
                                    console.log(response.success);
                                    /** Clear webpage info **/
                                    chrome.tabs.getSelected(null, function(tab) {
                                        var xhr = new XMLHttpRequest();
                                        xhr.open("POST", server + "/extension/info.php");
                                        xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded');
                                        xhr.send("");
                                    });
                                    /** Clear user info **/
                                    chrome.tabs.getSelected(null, function(tab) {
                                        var xhr = new XMLHttpRequest();
                                        xhr.open("POST", server + "/extension/qrcode.php");
                                        xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded');
                                        xhr.send("");
                                    });
                                });
                            });
                        } else {
                            ID = setTimeout(getInfo, 500);
                        }
                    } else {
                        ID = setTimeout(getInfo, 500);
                    }
                } else {
                    ID = setTimeout(getInfo, 500);
                }
            };
        };
        ID = setTimeout(getInfo, 500);
    });
}

var code = new QRCode(document.getElementById('code'), {
    width: 300,
    height: 300,
    useSVG: true,
    correctLevel: QRCode.CorrectLevel.H
});

var randomKey = rand(16);
var secretKey = rand(16);
var initVector = rand(16);

createCode();
