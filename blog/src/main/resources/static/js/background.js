
const userInfo = document.querySelector('.user-info-btn');
let userNickname = null;

// function that represent nickname with code
if (userInfo) {
    const userInfoContainer = document.querySelector(".user-info-container"); // For addEventListener
    const hoverMenu = document.getElementById("home-page-user-hover-menu"); // Show menu
    userInfoContainer.addEventListener('mouseover', () => {
        userInfo.textContent = 'Username: ' + userInfo.getAttribute('data-hover-text');
        hoverMenu.style.display = "block";
    })
    userInfoContainer.addEventListener('mouseout', () => {
        userInfo.textContent = "Username: " + userNickname;
        hoverMenu.style.display = "none";
    })
}

if (getCookie("Access_Token") != null) {
    let token = getCookie("Access_Token");
    localStorage.setItem("accessToken", token);
    deleteCookie("Access_Token");
}

const logoutBtn = document.querySelector(".logout-btn");
if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
        if (localStorage.getItem("accessToken") != null) {
            localStorage.removeItem("accessToken");
        }
        if (getCookie('Refresh_Token') != null) {
            deleteCookie('Refresh_Token');
        }
    })
}

// function httpViewRequest(url) {
//     let token = localStorage.getItem("accessToken");
//     let header = new Headers();
//     if (token != null) {
//         header.append("Authorization", "Bearer " + token)
//     }
//     let request = new Request(url, {
//         method: "GET",
//         headers: header
//     });
//
//     fetch(request)
//         .then(response => {
//             if (!response.ok) {
//                 throw new Error('Network response was not ok');
//             }
//             return response.text();
//         })
//         .then(html => {
//             // Create a temporary container element
//             let tempContainer = document.createElement('div');
//             tempContainer.innerHTML = html;
//
//             // Extract and execute JavaScript code
//             let scripts = tempContainer.getElementsByTagName('script');
//             for (let i = 0; i < scripts.length; i++) {
//                 eval(scripts[i].innerHTML);
//             }
//
//             // Append HTML content to the document body
//             document.body.innerHTML = tempContainer.innerHTML;
//         })
//         .catch(error => {
//             console.error('Error:', error);
//         });
// }

/**
 * Function that send request with access token, or with refresh token when access token is expired.
 *
 * @param url The url which you send request
 * @param method The method, CRUD
 * @param body The request body
 */
function httpApiRequest(url, method, body) {
    let token = localStorage.getItem("accessToken");
    let header = new Headers();
    if (token != null) {
        header.append("Authorization", "Bearer " + token);
    }
    header.append("Content-type", "application/json");
    let request = new Request(url, {
        method: method,
        headers: header,
        body: body
    });

    fetch(request)
        .then(response => {
            const refreshToken = getCookie('Refresh_Token');
            if (response.status === 401 && refreshToken) {
                fetch ('/api/token', {
                    method: 'POST',
                    headers: {
                        Authorization: "Bearer " + localStorage.getItem('access_token'),
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        refreshToken: getCookie('refresh_token')
                    })
                })
                    .then(response => {
                        if (response.status !== 200) {
                            throw new Error('Network response was not ok (' + response.status + ')');
                        }
                        return response.json()
                    })
                    .then(data => {
                        const token = data.accessToken;
                        localStorage.setItem('access_token', token)
                        httpApiRequest(method, url, body);
                    })
                    .catch(error => {
                        console.error('Error:', error);
                    });
            }
            else if (!response.ok && !(response.status === 201)) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

/**
 * Function which get cookie.
 *
 * @param key The name of cookie
 * @returns The value of cookie, When it doesn't exist, return null
 */
function getCookie(key) {
    let result = null;
    let cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');

        let dic = item.split('=');

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}

/**
 * Function which delete cookie
 * @param cookieName The name of cookie which will be deleted
 */
function deleteCookie(cookieName) {
    document.cookie = cookieName + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
}


//FIXME: 1. Change everything related to user info, 2. How to check logout.
getUser();

/**
 * Function that get user with fetch Api
 */
function getUser() {
    if (localStorage.getItem("accessToken") == null) {
        document.querySelector(".btn-container-login").style.display = "none";
        document.querySelector(".btn-container-not-login").style.display = "block";
    }
    else {
        fetch('/api/user', {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("accessToken")
            }
        })
            .then(response => {
                if (response.status !== 200) {
                    throw new Error("Http status is not ok");
                }
                return response.json();
            })
            .then(data => {
                setUserInfo(data.id, data.email, data.nicknameWithoutCode, data.nickname)
            })
    }
}

/**
 * Set User info in html
 * @param id The id of user
 * @param email The email of user
 * @param nicknameWithoutCode The nickname without code of user
 * @param nickname The nickname
 */
function setUserInfo(id, email, nicknameWithoutCode, nickname) {
    document.querySelector(".btn-container-login").style.display = "block";
    document.querySelector(".btn-container-not-login").style.display = "none";

    //login
    document.getElementById("user-id").value = id;
    document.getElementById("user-nickname").value = nicknameWithoutCode;
    userNickname = nicknameWithoutCode;
    document.querySelector(".user-info-btn").textContent = "Username: " + nicknameWithoutCode;
    document.querySelector(".user-info-btn").setAttribute("data-hover-text", nickname);
    document.getElementById("home-page-hover-menu-my-article")
        .setAttribute('href', "/home?nickname=" + nickname);

    //comment, reply
    let articleViewCommentAuthor = document.querySelector(".article-view-create-comment-body");
    let articleViewReplyAuthor = document.querySelector(".article-view-reply-author");
    articleViewCommentAuthor.querySelector("input").value = id;
    articleViewReplyAuthor.querySelector("input").value = id;
    articleViewCommentAuthor.querySelector("p").textContent = nicknameWithoutCode;
    articleViewReplyAuthor.querySelector("p").textContent = nicknameWithoutCode;
}