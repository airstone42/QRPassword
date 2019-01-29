function isUsername(item) {
    if (item.id.indexOf("username") != -1 ||
        item.id.indexOf("mail") != -1 ||
        item.name.indexOf("username") != -1 ||
        item.name.indexOf("mail") != -1)
        return true;
    else
        return false;
}

function isPassword(item) {
    if (item.id.indexOf("password") != -1 ||
        item.id.indexOf("passwd") != -1 ||
        item.name.indexOf("password") != -1 ||
        item.name.indexOf("passwd") != -1)
        return true;
    else
        return false;
}

chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
    var list = document.getElementsByTagName("input");
    // alert(request.username + request.password);
    for (var i = 0; i < list.length && list[i]; i++) {
        if (list[i].type == "text" && isUsername(list[i])) {
            list[i].value = atob(request.username);
        } else if (list[i].type == "password" && isPassword(list[i])) {
            list[i].value = atob(request.password);
        }
    }
    // sendResponse({success: true});
});
