const usernames = ["name", "mail", "Name", "Mail"]
const passwords = ["pass", "Pass"]

function isUsername(item) {
    let flag = false
    for (let username of usernames)
        flag = item.id.includes(username) || item.name.includes(username) || flag
    return flag
}

function isPassword(item) {
    let flag = false
    for (let password of passwords)
        flag = item.id.includes(password) || item.name.includes(password) || flag
    return flag
}

chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
    let list = document.getElementsByTagName('input')
    for (let i = 0; i < list.length && list[i]; i++) {
        if (list[i].type === "text" && isUsername(list[i])) {
            list[i].value = request.username
        } else if (list[i].type === "password" && isPassword(list[i])) {
            list[i].value = request.password
        }
    }
    sendResponse({success: true})
})
