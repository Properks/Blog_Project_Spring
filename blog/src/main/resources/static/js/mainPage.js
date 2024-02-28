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

// Show button related comment when mouseover
const commentBodies = document.querySelectorAll('.article-view-comment-body');
if (commentBodies) {
    commentBodies.forEach(comment => {
        let commentButtons = comment.querySelector('.article-view-comment-button-container');
        comment.addEventListener('mouseover', () => {
            commentButtons.style.display = 'inline-block';
        })

        comment.addEventListener('mouseout', () => {
            commentButtons.style.display = 'none';
        })
    })
}

//Show delete and modify button when author is same as user
const commentButtonContainer = document.querySelectorAll('.article-view-comment-button-container');
if (commentButtonContainer) {
    commentButtonContainer.forEach(button => {

        let authorId = button.closest('.article-view-comment-body')
            .querySelector('.article-view-comment-author')
            .querySelector('input').value;

        let userId = document.getElementById('user-id').value;
        let modifyButton = button.querySelector('.article-view-modify-comment-btn');
        let deleteButton = button.querySelector('.article-view-delete-comment-btn');

        if (userId !== authorId) {
            modifyButton.style.display = 'none';
            deleteButton.style.display = 'none';
        }
    })
}

function replyElement(element, nickname) {
    // article-view-reply-body
    let replyBody = document.createElement('div');
    replyBody.classList.add('article-view-reply-body');

    // article-view-reply-author
    let authorDiv = document.createElement('div');
    authorDiv.classList.add('article-view-reply-author');
    let authorInput = document.createElement('input');
    authorInput.type = 'hidden';
    authorInput.value = document.getElementById('user-id').value;
    let authorParagraph = document.createElement('p');
    authorParagraph.textContent = document.getElementById('user-nickname').value;
    authorDiv.appendChild(authorInput);
    authorDiv.appendChild(authorParagraph);

    // article-view-reply-content
    let contentDiv = document.createElement('div');
    contentDiv.classList.add('article-view-reply-content');
    let contentInput = document.createElement('input');
    // 필요에 따라 contentInput 속성 설정
    contentDiv.appendChild(contentInput);

    // article-view-reply-button-container
    let buttonContainerDiv = document.createElement('div');
    buttonContainerDiv.classList.add('article-view-reply-button-container');
    let cancelButton = document.createElement('button');
    cancelButton.classList.add('article-view-cancel-reply-btn');
    cancelButton.textContent = 'cancel';
    let submitButton = document.createElement('button');
    submitButton.classList.add('article-view-submit-reply-btn');
    submitButton.textContent = 'submit';
    buttonContainerDiv.appendChild(cancelButton);
    buttonContainerDiv.appendChild(submitButton);

    //append child
    replyBody.appendChild(authorDiv);
    replyBody.appendChild(contentDiv);
    replyBody.appendChild(buttonContainerDiv);

    //add element below element
    element.insertAdjacentElement('afterend', replyBody);
}

// check nickname is same as author. return boolean
function isSameAuthor(nickname) {
    let author = document.getElementById('article-view-author').textContent.replace("Writer: ", "");
    return author === nickname;
}