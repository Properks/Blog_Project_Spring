
// Fetch modify request from new article page
const modifyButton = document.getElementById('new-article-modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', ()=> {
        let articleId = document.getElementById('new-article-id').value;
        let articleTitle = document.getElementById('new-article-title').value;
        let articleContent = document.getElementById('new-article-content').value;
        let categoryId = document.getElementById('new-article-category').value;

        fetch('/api/article/' + articleId, {
            method : 'PUT',
            headers : {
                'Content-type' : 'application/json'
            },
            body : JSON.stringify({
                title: articleTitle,
                content: articleContent,
                category: categoryId
            })
        })
            .then(response => {
                if (response.status === 200) {
                    alert('Modify article successfully');
                    location.replace('/article/' + articleId);
                }
                else if (response.status === 400) {
                    alert('Fail to modify article (Bad Request)');
                    location.replace('/article/' + articleId);
                }
                else {
                    alert('Fail to modify article (Unknown error: ' + response.status + ')');
                    location.replace('/article/' + articleId);
                }
            })
    })
}

const createButton = document.getElementById('new-article-create-btn');

// Create article function in new article page
if (createButton) {
    createButton.addEventListener('click', ()=> {
        let title = document.getElementById('new-article-title').value;
        let content = document.getElementById('new-article-content').value;
        let categoryId = document.getElementById('new-article-category').value;

        fetch('/api/article', {
            method: 'POST',
            headers: {
                'Content-type': 'application/json'
            },
            body: JSON.stringify({
                title: title,
                content: content,
                categoryId: categoryId
            })
        })
            .then(response => {
                if (response.status === 201) {
                    alert('Create article successfully');
                    location.replace('/home');
                }
                else if (response.status === 400) {
                    alert('Fail to create article (Bad Request)')
                    location.replace('/new-article');
                }
                else {
                    alert('Fail to create article (Unknown error: ' + response.status + ')');
                    location.replace('/new-article');
                }
            })
    })
}

// check nickname is same as author. return boolean
function isSameAuthor(nickname) {
    let author = document.getElementById('article-view-author').textContent.replace("Writer: ", "");
    return author === nickname;
}