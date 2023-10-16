class CopyMenu{
    #from;
    #where;
    #confirmDiv;

    constructor(_from,_where) {
        this.from = _from;
        this.where = _where;

        document.getElementById("mainDiv").classList.add("blurActive");
        document.getElementById("mainDiv").classList.add("disablePoint");
        this.confirmDiv = document.createElement("div");
        this.confirmDiv.setAttribute("id","copyDiv");
        this.confirmDiv.classList.add("copyDiv");
    }


    displayMenu(){
        this.#addTitle("TAXONOMY ALTERATION");

        let codeDiv = document.createElement("div");
        codeDiv.classList.add("inner");
        this.#addCode("from: " + this.from, codeDiv);
        this.#addCode("to: " + this.where, codeDiv);
        this.confirmDiv.appendChild(codeDiv);

        this.#addText("select the desired operation:");

        let BTDiv = document.createElement("div");
        BTDiv.classList.add("inner");
        this.#addBt("dismiss", BTDiv, function (){
            document.body.removeChild(document.getElementById("copyDiv"));
            document.getElementById("mainDiv").classList.remove("blurActive");
            document.getElementById("mainDiv").classList.remove("disablePoint");
        });
        this.#addBt("copy", BTDiv,()=>this.#getNewTaxonomy(this.from,this.where,false));

        this.#addBt("cut", BTDiv,()=>this.#getNewTaxonomy(this.from,this.where,true));
        this.confirmDiv.appendChild(BTDiv);

        document.body.appendChild(this.confirmDiv);
    }
    #addTitle(title){
        let text  = document.createElement("h2");
        let textNode = document.createTextNode(title);
        text.appendChild(textNode);
        this.confirmDiv.appendChild(text);
    }

    #addText(label){
        let text  = document.createElement("p");
        let textNode = document.createTextNode(label);
        text.appendChild(textNode);
        this.confirmDiv.appendChild(text);
    }

    #addCode(label, inner){
        let fromCategory = document.createElement("div");
        fromCategory.classList.add("category");
        fromCategory.classList.add("actionCode");
        let textNode = document.createTextNode(label);
        fromCategory.appendChild(textNode);
        inner.appendChild(fromCategory);
    }

    #addBt(label, inner, action){
        let BT = document.createElement("button");
        BT.addEventListener("click",action);
        let textNode = document.createTextNode(label);
        BT.appendChild(textNode);
        BT.classList.add("actionBt");
        inner.appendChild(BT);
    }

    #getNewTaxonomy(from,where,remove){
        doGet(getContextPath()+"/taxonomyModification"+"?from="+from+"&where="+where+"&remove="+remove, null, function (req) {
            if(req.readyState===4){
                let message = req.responseText;
                switch (req.status) {
                    case 200:
                        fillTable(JSON.parse(req.responseText), true);
                        new ConfirmMenu(from,where,remove).displayMenu();
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
                document.body.removeChild(document.getElementById("copyDiv"));
                document.getElementById("mainDiv").classList.remove("blurActive");
                document.getElementById("mainDiv").classList.remove("disablePoint");
            }
        });
    }
}