var code = new QRCode(document.getElementById('code'), {
    width: 300,
    height: 300,
    useSVG: true,
    correctLevel: QRCode.CorrectLevel.H
});

var randkey = rand(16);

function rand(n){
	var rnd = "";
	for(var i = 0; i < n; i++)
		rnd += Math.floor(Math.random() * 10);
	return rnd;
}

function createCode() {
    var info = new String("");
    chrome.tabs.getSelected(null, function(tab) {
        var url = new URL(tab.url);
        var codeContent = {"hostname": btoa(url.hostname), "randkey": btoa(randkey)};
        var codeText = JSON.stringify(codeContent)
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "http://133.130.97.7/plugin/qrcode.php");
        xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded');
        xhr.send(codeText);
        code.makeCode(codeText);
        var ID = 0;
        var getInfo = function() {
            var xhr = new XMLHttpRequest();
            xhr.open("GET", "http://133.130.97.7/plugin/getinfo.php");
            xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded');
            xhr.send();
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    if (xhr.responseText != "") {
                        var infoJSON = JSON.parse(xhr.responseText);
                        if (infoJSON.randkey == btoa(randkey)) {
                            clearTimeout(ID);
                            // alert("username: " + infoJSON.username + " password: " + infoJSON.password);
                            chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
                                chrome.tabs.sendMessage(tabs[0].id, {username: infoJSON.username, password: infoJSON.password}, function(response) {
                                    console.log(response.success);
                                });
                            });
                            chrome.tabs.getSelected(null, function(tab) {
                                var xhr = new XMLHttpRequest();
                                xhr.open("POST", "http://133.130.97.7/plugin/setinfo.php");
                                xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded');
                                xhr.send("");
                            });
                            chrome.tabs.getSelected(null, function(tab) {
                                var xhr = new XMLHttpRequest();
                                xhr.open("POST", "http://133.130.97.7/plugin/qrcode.php");
                                xhr.setRequestHeader('Content-Type',' application/x-www-form-urlencoded');
                                xhr.send("");
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
            }
        }
        ID = setTimeout(getInfo, 500);
    });
}

createCode();
