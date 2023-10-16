{
    let userDataDiv = document.getElementById("userDataDiv");
    let listContainer = document.getElementById("listContainer");
    let pasteOnTop = document.getElementById("pasteOnTop");

    let addCategoryForm = document.getElementById("addCategoryForm");
    let addCategoryButton = addCategoryForm.querySelector("input[type='button']");

    let searchBar = document.getElementById("searchBar");
    let searchCategoryButton = document.getElementById("searchCategoryButton");

    let logoutBt = document.getElementById("logoutBt");
    let draggedID;

    window.addEventListener("load", function() {

        fillInfo();

        addCategoryButton.addEventListener("click", function(e) {
            new AddCategoryForm(e.target.closest("form")).sendForm("addCategory");
        });
        addCategoryForm.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') new AddCategoryForm(e.target.closest("form")).sendForm("addCategory");
        });

        logoutBt.addEventListener("click", function (){
            doLogout();
        });

        searchCategoryButton.addEventListener("click",function (e) {
            new SearchForm().sendForm();
        });
        searchBar.addEventListener('keypress', function (e) {
            if (e.key === 'Enter')new SearchForm().sendForm();
        });


        pasteOnTop.addEventListener('drop', ()=>{
            pasteOnTop.classList.remove("SelectedInner");
            new CopyMenu(draggedID,0).displayMenu();
        });
        pasteOnTop.addEventListener('dragover', e=>{
            e.preventDefault();
            pasteOnTop.classList.add("SelectedInner");
        });
    }, false);

    function fillInfo(clear=false){
        doGet(getContextPath()+"/mainPage", null, function (req) {
            if(req.readyState === 4){
                let getData =  JSON.parse(req.responseText);
                if(!clear)fillUserInfo(getData.userData);
                fillTable(getData.categories,clear);
            }
        });
    }

    function fillTable(categories, clear = false){
        while (clear && listContainer.firstChild){
            listContainer.removeChild(listContainer.firstChild);
        }
        for(let i = 0; i < categories.length; i++) {
            let category = new Category(categories[i].code,categories[i].name, categories[i].padding, categories[i].state);
            category.display();
        }
    }

    function fillUserInfo(userInfo){
        let p  = document.createElement("p");
        userDataDiv.appendChild(p);
        let textNode = document.createTextNode(userInfo.username);
        p.appendChild(textNode);

        p  = document.createElement("p");
        p.classList.add("data2");
        userDataDiv.appendChild(p);
        textNode = document.createTextNode(userInfo.email);
        p.appendChild(textNode);

        p  = document.createElement("p");
        userDataDiv.appendChild(p);
        if(userInfo.role>=1) textNode = document.createTextNode("admin");
        else textNode = document.createTextNode("guest");
        p.appendChild(textNode);
    }

    function doLogout(){
        doGet(getContextPath()+"/logout", null, function (req) {
            if(req.readyState === 4){
                document.cookie = "username= ; expires = Thu, 01 Jan 1970 00:00:00 GMT"
                window.location.replace(getContextPath() + "index.html");
            }
        });
    }

    class Category{
        #code;
        #name;
        #nameP;
        #padding;
        #state ;
        #innerDiv;
        #categoryDiv;
        #categoryDiv2;

        constructor(_code, _name, _padding, _state ) {
            this.#code = _code;
            this.#name = _name;
            this.#padding = _padding;
            this.#state = _state ;

            this.#innerDiv = document.createElement("div");

            if(this.#state==="REMOVED")this.#innerDiv.style.opacity = "0.5";

            this.#innerDiv.style.marginLeft = this.#padding + "px";
            this.#innerDiv.setAttribute('draggable', true);
            this.#innerDiv.setAttribute('id', this.code);
            this.#innerDiv.classList.add("inner");
            this.#categoryDiv = document.createElement("div");
            this.#categoryDiv.classList.add("category");
            this.#innerDiv.appendChild(this.#categoryDiv);
            this.#categoryDiv2 = document.createElement("div");
            this.#categoryDiv2.classList.add("category");

            this.#innerDiv.appendChild(this.#categoryDiv2);
        }

        #displayCode(){
            let code  = document.createElement("p");
            code.classList.add("categoryCode");
            if(this.#state==="SELECTED") code.classList.add("categoryCodeSelected");
            else if(this.#state==="UPDATED") code.classList.add("categoryUpdate");
            else if(this.#state==="REMOVED") code.classList.add("categoryRemove");

            this.#categoryDiv.appendChild(code);
            let textNode = document.createTextNode(this.#code);
            code.appendChild(textNode);
        }

        #displayName(action){
            this.#nameP = document.createElement("p");
            this.#nameP.addEventListener("click",action);
            this.#nameP.classList.add("categoryName");
            let textNode = document.createTextNode(this.#name);
            this.#nameP.appendChild(textNode);
            this.#categoryDiv2.appendChild(this.#nameP);
        }

        #updateName(nameInput){
            this.#name = nameInput.value;
            this.#categoryDiv2.replaceChild(this.#nameP,nameInput);
            doPost(getContextPath()+"/updateCatName?code="+this.#code+"&newName="+this.#name,null,function (){});
            this.#nameP.innerText = this.#name;
        }

        #displayIcon(iconName, iconContainer, action){
            let iconDiv = document.createElement('div');
            iconDiv.classList.add("category");
            iconContainer.appendChild(iconDiv)
            let i = document.createElement('i');
            i.addEventListener("click",action);
            i.className = 'material-icons';
            let text = document.createTextNode(iconName);
            i.appendChild(text);
            iconDiv.appendChild(i)
        }

        #dragListenerInit(){
            this.#innerDiv.addEventListener('dragstart', ()=>{
                pasteOnTop.style.display = "block";
                this.#innerDiv.classList.add("dragInner");
                draggedID = this.#code;
            })
            this.#innerDiv.addEventListener('dragend', ()=>{
                pasteOnTop.style.display = "none";
                this.#innerDiv.classList.remove("dragInner");
                draggedID = null;
            })
            this.#innerDiv.addEventListener('dragover', e=>{
                e.preventDefault();
                this.#innerDiv.classList.add("SelectedInner");
            })
            this.#innerDiv.addEventListener('dragleave', e=>{
                e.preventDefault();
                this.#innerDiv.classList.remove("SelectedInner");
            })
            this.#innerDiv.addEventListener('drop', ()=>{
                pasteOnTop.style.display = "none";
                this.#innerDiv.classList.remove("SelectedInner");
                new CopyMenu(draggedID,this.#code).displayMenu();
            })
        }

        #modifyName(){
            let nameInput = document.createElement("input");
            nameInput.addEventListener("focusout",()=>this.#updateName(nameInput));
            this.#categoryDiv2.replaceChild(nameInput,this.#nameP);
            nameInput.value = this.#name;
            nameInput.focus();
        }

        display(){
            this.#displayName(()=>this.#modifyName(this.#name));
            this.#displayCode();
            this.#dragListenerInit();

            let iconContainer = document.createElement('div');
            iconContainer.classList.add("iconContainer");
            this.#displayIcon("add_circle",iconContainer, ()=>document.getElementById("fatherCode").value= this.#code);
            this.#displayIcon("delete_forever",iconContainer,()=>new removeCategory(this.#code).remove());
            this.#innerDiv.appendChild(iconContainer);

            listContainer.appendChild(this.#innerDiv);
        }
    }

    class removeCategory{
        #code
        constructor(_code) {
            this.#code=_code;
        }

        remove(){
            doPost(getContextPath()+ "/removeCat?code="+this.#code,null,function (req){
                if(req.readyState===4) {
                    let message = req.responseText;
                    switch (req.status) {
                        case 200:
                            fillTable(JSON.parse(req.responseText), true);
                            break;
                        case 400: // bad request
                            alert(message);
                            break;
                        case 401: // unauthorized
                            alert(message);
                            break;
                        case 500: // server error
                            alert(message);
                            break;
                    }
                }
            })
        }
    }

    class AddCategoryForm{
        #addCategoryForm;
        constructor(_addCategoryForm) {
            this.addCategoryForm = _addCategoryForm;
        }
        validate(){
            return !(document.getElementsByName("categoryName")[0].value==="");
        }

        sendForm(url){
            if(!this.validate()) {
                alert("please fill all the mandatory fields");
                return;
            }

            doPost(url, this.addCategoryForm, function (req) {
                if(req.readyState === 4){
                    let message = req.responseText;
                    switch (req.status) {
                        case 200:
                            fillTable(JSON.parse(req.responseText), true);
                            break;
                        case 400: // bad request
                            alert(message);
                            break;
                        case 401: // unauthorized
                            alert(message);
                            break;
                        case 500: // server error
                            alert(message);
                            break;
                    }
                }
            }, true);
        }
    }

    class SearchForm{
        #inputValue
        validate(){
            return !(this.#inputValue==="");
        }
        sendForm(){
            this.#inputValue = document.getElementById("searchBar").value;

            if(!this.validate()){
                alert("type something in the search bar before");
                return;
            }

            doGet(getContextPath() +"/searchCategory?search="+ document.getElementById("searchBar").value,null,function (req) {
                if (req.readyState === XMLHttpRequest.DONE) { // response has arrived
                    let message = req.responseText;
                    switch (req.status) {
                        case 200:
                            fillTable(JSON.parse(req.responseText), true);

                            if(!document.getElementById("addDelBt"))addDelBt(()=>{
                                    fillInfo(true);
                                    document.getElementById("searchCategory").removeChild(document.getElementById("addDelBt"));
                                    document.getElementById("searchBar").value="";
                            });

                            break;
                        case 400: // bad request
                            alert(message);
                            break;
                        case 401: // unauthorized
                            alert(message);
                            break;
                        case 500: // server error
                            alert(message);
                            break;
                    }
                }
            });

            let addDelBt = function (action){

                console.log("test")

                let bt = document.createElement("i");
                bt.setAttribute("id","addDelBt");
                bt.className = 'material-icons';
                bt.classList.add("delSearch");
                bt.addEventListener("click",action);
                let text = document.createTextNode("cancel");
                bt.appendChild(text);
                document.getElementById("searchCategory").appendChild(bt);
            }

        }
    }
}