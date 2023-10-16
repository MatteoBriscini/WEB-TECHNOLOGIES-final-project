class ConfirmMenu{
    #from;
    #where;
    #remove;
    #confirmDiv;
    constructor(_from,_where,_remove) {
        this.#from = _from;
        this.#where = _where;
        this.#remove = _remove;

        this.#confirmDiv = document.createElement("div");
        this.#confirmDiv.setAttribute("id","confirm");
        this.#confirmDiv.classList.add("confirmDiv");
    }

    displayMenu(){
        this.#addTitle("ALERT!");
        this.#addText("this is a local taxonomy click save to make it available to other users");
        this.#addText("color legend:", "rgb(102, 102, 102)");

        let legendDiv = document.createElement("div");
        legendDiv.classList.add("legendDiv");
        this.#addLegendElement("red","will be removed",legendDiv);
        this.#addLegendElement("var(--error-color)","will be updated",legendDiv);
        this.#addLegendElement("var(--selected-color)","will be added",legendDiv);

        this.#confirmDiv.appendChild(legendDiv);

        let BTDiv = document.createElement("div");
        BTDiv.classList.add("inner");
        this.#addBt("cancel",BTDiv,()=>this.#cancelModification());
        this.#addBt("save",BTDiv,()=>this.#confirmModification(this.#from,this.#where,this.#remove));
        this.#confirmDiv.appendChild(BTDiv);

        document.getElementById("addForm").style.display = "none";
        document.getElementById("gridContainer").appendChild(this.#confirmDiv);
        document.getElementById("listContainer").classList.add("disablePoint");
    }
    #addTitle(title){
        let text  = document.createElement("h2");
        let textNode = document.createTextNode(title);
        text.appendChild(textNode);
        this.#confirmDiv.appendChild(text);
    }
    #addBt(label, inner, action){
        let BT = document.createElement("button");
        BT.addEventListener("click",action);
        let textNode = document.createTextNode(label);
        BT.appendChild(textNode);
        BT.classList.add("actionBt");
        inner.appendChild(BT);
    }
    #addText(label, color="white"){
        let text  = document.createElement("p");
        let textNode = document.createTextNode(label);
        text.style.color= color;
        text.appendChild(textNode);
        this.#confirmDiv.appendChild(text);
    }
    #addLegendElement(color, label, legendDiv){
        let element = document.createElement("div");
        element.classList.add("legendElement");

        let pill = document.createElement("div");
        pill.classList.add("pill");
        pill.style.backgroundColor = color;
        pill.classList.add("legendElementContent");
        element.appendChild(pill);

        let text  = document.createElement("p");
        text.classList.add("legendElementContent");
        let textNode = document.createTextNode(label);
        text.appendChild(textNode);
        element.appendChild(text);
        legendDiv.appendChild(element);
    }
    #cancelModification(){
        doGet(getContextPath()+"/mainPage", null, function (req) {
            if(req.readyState === 4){
                let getData =  JSON.parse(req.responseText);
                fillTable(getData.categories,true);

                document.getElementById("gridContainer").removeChild(document.getElementById("confirm"));
                document.getElementById("addForm").style.display = "block";
                document.getElementById("listContainer").classList.remove("disablePoint");
            }
        });
    }

    #confirmModification(from,where,remove){
        doPost(getContextPath()+"/taxonomyModification"+"?from="+from+"&where="+where+"&remove="+remove, null, function (req) {
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

                document.getElementById("gridContainer").removeChild(document.getElementById("confirm"));
                document.getElementById("addForm").style.display = "block";
                document.getElementById("listContainer").classList.remove("disablePoint");
            }
        });
    }
}