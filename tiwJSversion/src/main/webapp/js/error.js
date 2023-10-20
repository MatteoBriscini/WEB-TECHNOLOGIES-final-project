{
    let returnLink = document.getElementById("returnLink");


    window.addEventListener("load", function() {
        let queryString = window.location.search;
        let urlParams = new URLSearchParams(queryString);


        let textNode = document.createTextNode(urlParams.get('errorMSG'));
        document.getElementById('errorMSG').appendChild(textNode);

        textNode = document.createTextNode(urlParams.get('errorTitle'));
        document.getElementById('errorTitle').appendChild(textNode);

        returnLink.addEventListener("click", function(){
            window.location.replace(getContextPath() + "mainPage.html");
        })
    });
}