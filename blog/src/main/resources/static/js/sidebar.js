// Add root category
const categoryButton = document.getElementById("create-sidebar-btn");
if (categoryButton) {
    categoryButton.addEventListener('click', () => {
        let name = prompt("Enter a new category");
        fetch('/api/category', {
            method: 'POST',
            headers: {
                'Content-type': 'application/json'
            },
            body: JSON.stringify({
                parentId: null,
                name: name
            })
        })
            // fetch('/api/category/' + path, {method: 'POST'})
            .then(response => {
                if (response.status === 201) {
                    alert("Success to create category");
                    location.replace('/home');
                }
                else if (response.status === 400) {
                    alert('Fail to create category (Bad Request)');
                    location.replace('/home');
                }
                else {
                    alert('Fail to create category (Unknown error: ' + response.status + ')');
                    location.replace('/home');
                }
            })

    })
}

//Add child category
const createButtons = document.querySelectorAll('.create-category-btn');
if (createButtons) {
    createButtons.forEach(function (button) {
        button.addEventListener('click', function () {
            let parentLi = button.closest('li');
            let parentId = parentLi.querySelector('input').value;
            console.log(parentId);

            let name = prompt("Enter category name");
            console.log(name)
            fetch('/api/category', {
                method: 'POST',
                headers: {
                    'Content-type': 'application/json'
                },
                body: JSON.stringify({
                    parentId: parentId,
                    name: name
                })
            })
                .then(response => {
                    if (response.status === 201) {
                        alert("Success to create category");
                        location.replace('/home');
                    }
                    else if (response.status === 400) {
                        alert('Fail to create category (Bad Request)');
                        location.replace('/home');
                    }
                    else {
                        alert('Fail to create category (Unknown error: ' + response.status + ')');
                        location.replace('/home');
                    }
                })
        });
    });
}

//category select
const sidebarElements = document.querySelectorAll(".category-name");
if (sidebarElements) {
    sidebarElements.forEach(function (name) {
        name.addEventListener('click', () => {
            let liElement = name.closest('li');
            let categoryId = liElement.querySelector(".category-id").value;
            let url = new URLSearchParams(location.search);
            url.set('categoryId', categoryId);
            location.replace("/home?" + url.toString())
        })
    })
}

//Show category in article list
//FIXME: It has error except first element.
const categoryInUrl = new URLSearchParams(location.search).get('categoryId');
const showCategoryInList = document.getElementById('show-selected-category');
if (showCategoryInList) {
    if (categoryInUrl) {
        let idElements = document.querySelector('.category-id');
        let categoryId = Array(idElements).find(item => item.value === categoryInUrl);
        let liElement = categoryId.closest('li');
        let categoryName = liElement.querySelector('.category-name').textContent;
        showCategoryInList.textContent = 'Category: ' + categoryName;
    } else {
        showCategoryInList.textContent = 'Category: All';
    }
}

//All category
const allCategory = document.getElementById('sidebar-all-category');
if (allCategory) {
    allCategory.addEventListener('click', () => {
        let url = new URLSearchParams(location.search);
        let query = 'categoryId';
        if (url.has(query)) {
            url.delete(query)
        }
        location.replace('/home?' + url.toString());
    })
}

