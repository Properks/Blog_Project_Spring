
const userInfo = document.querySelector('.user-info-btn');
const userNickname = (userInfo) ? userInfo.textContent.replace("Username: ", "") : null;

// function that represent nickname with code
if (userInfo) {
    const userInfoContainer = document.querySelector(".user-info-container"); // For addEventListener
    const userOriginalNickname = userInfo.textContent;
    const hoverMenu = document.getElementById("home-page-user-hover-menu"); // Show menu
    userInfoContainer.addEventListener('mouseover', () => {
        userInfo.textContent = 'Username: ' + userInfo.getAttribute('hover-text');
        hoverMenu.style.display = "block";
    })
    userInfoContainer.addEventListener('mouseout', () => {
        userInfo.textContent = userOriginalNickname;
        hoverMenu.style.display = "none";
    })
}

const urlParam = new URLSearchParams(location.search);
if (urlParam.has("token")) {
    let token = urlParam.get("token");
    localStorage.setItem("accessToken", token);
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
            const refreshToken = getCookie('refresh_token');
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
                    .then(response => response.json())
                    .then(response => {
                        const token = response.accessToken
                        localStorage.setItem('access_token', token)
                        httpApiRequest(method, url, body);
                    })
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

function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');

        var dic = item.split('=');

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}
