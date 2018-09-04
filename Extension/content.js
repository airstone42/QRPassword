chrome.runtime.onMessage.addListener( function(request, sender, sendResponse) {
    var list = document.getElementsByTagName("input");
    for (var i = 0; i < list.length && list[i]; i++) {
        if (list[i].type == "text" && (list[i].id.indexOf("username") != -1 || list[i].name.indexOf("username") != -1)) {
            list[i].value = atob(request.username);
        } else if (list[i].type == "password" && (list[i].id.indexOf("password") != -1 ||
                                                  list[i].id.indexOf("passwd") != -1 ||
                                                  list[i].name.indexOf("password") != -1 ||
                                                  list[i].name.indexOf("passwd") != -1)) {
            list[i].value = atob(request.password);
        }
    }
    // sendResponse({success: true});
});
