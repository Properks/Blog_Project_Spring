// fetch delete request from article view page
const deleteButton = document.getElementById('article-view-delete-article-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', ()=> {
        if (isSameAuthor(userNickname)) {
            if (confirm('Delete this article?')) {
                let articleId = document.getElementById('article-view-id').value;
                fetch('/api/article/' + articleId, {method: 'DELETE'})
                    .then(response => {
                        if (response.status === 200) {
                            alert('Delete article successfully');
                            location.replace('/home');
                        }
                        else if (response.status === 400) {
                            alert('Fail to delete article (Bad Request)');
                            location.replace('/article/' + articleId);
                        }
                        else {
                            alert('Fail to delete article (Unknown error: ' + response.status + ')');
                            location.replace('/article/' + articleId);
                        }
                    })
            }
        } else {alert("Cannot delete someone else's");}
    })
}

// check you can go to modify page
const modifyButtonInArticleView = document.getElementById('article-view-modify-article-btn');
if (modifyButtonInArticleView) {
    modifyButtonInArticleView.addEventListener('click', ()=> {
        if(isSameAuthor(userNickname)) {
            let articleId = document.getElementById('article-view-id').value;
            location.replace('/new-article?id=' + articleId);
        } else {alert("Cannot modify someone else's")}
    })
}

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

//create comment button
const createCommentBtn = document.getElementById('article-view-create-comment-btn');
if (createCommentBtn) {
    createCommentBtn.addEventListener('click', () => {
        let content = document.getElementById('article-view-create-comment-content').value;
        if (content === '') {
            alert('Comment is empty!');
        }
        else {
            let articleId = document.getElementById('article-view-id').value;
            let currentURL = location.href;
            fetch('/api/comment', {
                method: 'POST',
                headers: {
                    'Content-type': 'application/json'
                },
                body: JSON.stringify({
                    articleId: articleId,
                    parent: null,
                    content: content
                })
            })
                .then((response) => {
                    if (response.status === 201) {
                        alert('Create comment successfully');
                    }
                    else {
                        alert('Fail to create comment');
                    }
                    location.replace(currentURL);
                })
        }
    })
}

// check nickname is same as author. return boolean
function isSameAuthor(nickname) {
    let author = document.getElementById('article-view-author').textContent.replace("Writer: ", "");
    return author === nickname;
}