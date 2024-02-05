
// Change page
const beforePage = document.getElementById('article-list-page-decrease');
const afterPage = document.getElementById('article-list-page-increase');
const totalPage = document.getElementById('article-list-total-page');
const currentPage = document.querySelector('.current-page');
const otherPage = document.querySelectorAll('.other-page')

if (beforePage && currentPage.textContent !== '1') {
    beforePage.addEventListener('click', () => {
        let url = new URLSearchParams(location.search);
        url.set('page', (parseInt(currentPage.textContent) - 1).toString());
        location.replace('/home?' + url.toString());
    })
}

if (afterPage && currentPage.textContent !== totalPage.value) {
    afterPage.addEventListener('click', () => {
        let url = new URLSearchParams(location.search);
        url.set('page', (parseInt(currentPage.textContent) + 1).toString())
        location.replace('/home?' + url.toString());
    })
}

if (otherPage) {
    otherPage.forEach(element => {
        element.addEventListener('click', () => {
            let url = new URLSearchParams(location.search);
            url.set('page', element.textContent);
            location.replace('/home?' + url.toString());
        })
    })
}

//Change articles per page
const articlesPerPage = document.getElementById('article-list-page-size');
if (articlesPerPage) {
    let sizeParameter = new URLSearchParams(location.search).get('size');
    if (sizeParameter) {
        articlesPerPage.value = sizeParameter;
    } else {articlesPerPage.value = 10;}

    articlesPerPage.addEventListener('change', () => {
        let url = new URLSearchParams(location.search);
        url.set('size', articlesPerPage.value);
        location.replace("/home?" + url.toString());
    })
}

//Search article
const searchButton = document.getElementById('article-list-search-btn');
if (searchButton) {
    searchButton.addEventListener('click', () => {
        let searchCategory = document.getElementById('article-list-search-category').value;
        let searchQuery = document.getElementById('article-list-search-parameter').value;
        let url = new URLSearchParams(location.search);
        let hasQueryInURL = url.has('titleContent') || url.has('writer');
        if (searchQuery !== "") {
            addSearchQueryInURL(url, searchCategory, searchQuery)
            location.replace('/home?' + url.toString());
        }
        else if (hasQueryInURL) {
            url.delete((url.has('titleContent')) ? 'titleContent' : 'writer');
            location.replace('/home?' + url.toString())
        }
    })
}

function addSearchQueryInURL(urlParam, category, query) {
    if (urlParam.has('titleContent') && category === 'writer') {
        urlParam.delete('titleContent');
    }
    else if (urlParam.has('writer') && category === 'titleContent') {
        urlParam.delete('writer');
    }
    urlParam.set(category, query);
}

//Show searched word
const searchedWordInURL = new URLSearchParams(location.search);
const searchedWord = document.getElementById('article-list-searched-word');
if (searchedWord) {
    if (searchedWordInURL.has('titleContent')) {
        searchedWord.textContent = searchedWordInURL.get('titleContent');
    }
    else if (searchedWordInURL.has('writer')) {
        searchedWord.textContent = searchedWordInURL.get('writer');
    }
    else {
        searchedWord.textContent = 'none';
    }
}
