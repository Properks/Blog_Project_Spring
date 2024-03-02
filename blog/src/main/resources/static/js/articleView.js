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

//Comments
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
                        alert('Fail to create comment (ERROR: ' + response.status + ")");
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

//delete
const deleteCommentButtons = document.querySelectorAll('.article-view-delete-comment-btn');
if (deleteCommentButtons) {
    deleteCommentButtons.forEach(button =>
        button.addEventListener('click', () => {
            if (confirm("Do you want to delete the comment?")) {
                let commentId = button.closest('.article-view-comment-body').querySelector('.article-view-comment-id').value;
                let currentURL = location.href;
                fetch('/api/comment/' + commentId, {method: 'DELETE'})
                    .then(response => {
                        if (response.status === 200) {
                            alert('Delete it successfully');
                        }
                        else {
                            alert('Fail to delete (ERROR: ' + response.status + ")");
                        }
                        location.replace(currentURL);
                    })
            }
        }))
}


//modify
const modifyCommentButtons = document.querySelectorAll('.article-view-modify-comment-btn');
if (modifyCommentButtons) {
    modifyCommentButtons.forEach(button => {
        button.addEventListener('click', () => {
            closeAllOfModifyContainer();
            let commentBody =  button.closest('.article-view-comment-body');
            let commentContent = commentBody.querySelector('.article-view-comment-content');
            let modifyContainer = commentBody.querySelector('.article-view-modify-comment-container');
            commentContent.style.display = 'none';
            modifyContainer.style.display = 'block';
        })
    })
}
const modifyCancelButtons = document.querySelectorAll('.article-view-cancel-modify-comment-btn');
if (modifyCancelButtons) {
    modifyCancelButtons.forEach(button => {
        button.addEventListener('click', () => {
            closeAllOfModifyContainer();
        })
    })
}

const modifySubmitButtons = document.querySelectorAll('.article-view-submit-modify-comment-btn');
if (modifySubmitButtons) {
    modifySubmitButtons.forEach(button => {
        button.addEventListener('click', () => {
            let commentBody = button.closest('.article-view-comment-body')
            let commentId = commentBody.querySelector('.article-view-comment-id').value;
            let modifiedContent = commentBody.querySelector('.article-view-modify-comment-textarea').value;

            let currentURL = location.href;

            fetch('/api/comment', {
                method: 'PUT',
                headers: {
                    'Content-type': 'application/json'
                },
                body: JSON.stringify({
                    commentId: commentId,
                    content: modifiedContent
                })
            })
                .then(response => {
                    if (response.status === 200) {
                        alert('Update comment successfully');
                    }
                    else {
                        alert('Fail to update comment (ERROR: ' + response.status + ")");
                    }
                    location.replace(currentURL);
                })
        })
    })
}

//reply
const replyButtons = document.querySelectorAll('.article-view-create-reply-btn');
if (replyButtons) {
    replyButtons.forEach(button => {
        button.addEventListener('click', () => {
            let commentBody = button.closest('.article-view-comment-body');
            let commentAuthor = commentBody.querySelector('.article-view-author-nickname').textContent;
            closeAllOfReplyContainer();
            replyElement(commentBody, commentAuthor);
        })
    })
}

function closeAllOfModifyContainer() {
    commentBodies.forEach(body => {
        body.querySelector('.article-view-comment-content').style.display = 'block';
        body.querySelector('.article-view-modify-comment-container').style.display = 'none';
    })
}

function closeAllOfReplyContainer() {
    let container = document.querySelector('.article-view-comment-container');
    let replyBodies = container.querySelectorAll('.article-view-reply-body');
    replyBodies.forEach(body => {
        body.remove();
    })
}

function replyElement(element, nickname) {
    let replyBody = document.querySelector('.article-view-reply-body').cloneNode(true);
    replyBody.style.display = 'flex';
    replyBody.querySelector('.article-view-reply-textarea').textContent = "@" + nickname + " ";


    //add element below element
    element.insertAdjacentElement('afterend', replyBody);
}
