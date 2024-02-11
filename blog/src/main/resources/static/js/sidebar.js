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

            let name = prompt("Enter category name");
            let currentURL = location.href;
            if (name !== "") {
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
                            location.replace(currentURL);
                        }
                        else if (response.status === 400) {
                            alert('Fail to create category (Bad Request)');
                            location.replace(currentURL);
                        }
                        else {
                            alert('Fail to create category (Unknown error: ' + response.status + ')');
                            location.replace(currentURL);
                        }
                    })
            }
        });
    });
}

//Delete category
const deleteButtons = document.querySelectorAll('.delete-category-btn');
if (deleteButtons) {
    deleteButtons.forEach(button => {
        button.addEventListener('click', () => {
            let liElement = button.closest('li');
            let categoryId = liElement.querySelector('.category-id').value;
            let categoryName = findCategoryNameWithId(categoryId);
            let currentURL = location.href;
            if (confirm("Are you sure you want to delete " + categoryName + "?")) {
                fetch('/api/category/' + categoryId , {method: 'DELETE'})
                    .then(response => {
                        if (response.status === 200) {
                            alert('Success to delete category');
                            location.replace(currentURL);
                        }
                        else if (response.status === 400) {
                            alert('Fail to delete category (Bad Request)');
                            location.replace(currentURL);
                        }
                        else {
                            alert('Fail to delete category (Unknown error: ' + response.status + ')');
                            location.replace(currentURL);
                        }
                    })
            }
        })
    })
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
const categoryInUrl = new URLSearchParams(location.search).get('categoryId');
const showCategoryInList = document.getElementById('show-selected-category');
if (showCategoryInList) {
    if (categoryInUrl) {
        let categoryName = findCategoryNameWithId(categoryInUrl);
        showCategoryInList.textContent = 'Category: ' + categoryName;
    } else {
        showCategoryInList.textContent = 'Category: All';
    }
}

//Get category name with category id
function findCategoryNameWithId(categoryId) {
    let idElements = document.querySelector('.category-id[value="' + categoryId + '"]');
    let liElement = idElements.closest('li');
    return liElement.querySelector('.category-name').textContent;
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

//Edit mode
const editButton = document.getElementById('sidebar-edit-btn');
const editDoneButton = document.getElementById('sidebar-edit-done-btn');
const newCategoryButton = document.getElementById('create-sidebar-btn');
const deleteCategoryButton = document.querySelectorAll('.delete-category-btn');
if (userInfo) {
    showElement(editButton);
    editButton.addEventListener('click', () => {
        openEditMode()
    })

    editDoneButton.addEventListener('click', () => {
        closeEditMode()
    })
}

function hideElement(element) {
    element.style.display = 'none';
}

function showElement(element) {
    element.style.display = 'block';
}

function openEditMode() {
    deleteCategoryButton.forEach(item => showElement(item))
    createButtons.forEach(item => showElement(item));
    showElement(newCategoryButton);
    showElement(editDoneButton);
    hideElement(editButton);
}

function closeEditMode() {
    deleteCategoryButton.forEach(item => hideElement(item))
    createButtons.forEach(item => hideElement(item));
    hideElement(newCategoryButton);
    hideElement(editDoneButton);
    showElement(editButton);
}

