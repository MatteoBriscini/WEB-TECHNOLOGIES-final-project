function makeCall(method, url, formElement, cBack, reset = true) {
    HTTPRequest(method, url, formElement, cBack);
    if (formElement !== null && reset === true) {
        formElement.reset();
    }
}

function HTTPRequest(method, url, formData, cBack){
    if(!(formData instanceof FormData) && formData!=null)formData = new FormData(formData);
    let req = new XMLHttpRequest(); // visible by closure
    req.onreadystatechange = function() {
        cBack(req);
    }; // closure
    req.open(method, url);
    req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    if (formData === null) {
        req.send();
    } else {
        let sendValue = parser(formData);
        req.send(sendValue);
    }
}

function doPost(url, formElement, cBack, reset = true){
    makeCall("POST", url, formElement, cBack, reset);
}

function doGet(url, formElement, cBack, reset = true){
    makeCall("GET", url, formElement, cBack, reset);
}

function parser(formData){
    const urlEncodedDataPairs = [];
    for(const [key,value]of formData){
        urlEncodedDataPairs.push(
            `${encodeURIComponent(key)}=${encodeURIComponent(value)}`,
        );
    }
    return urlEncodedDataPairs.join("&").replace(/%20/g, "+");
}

function getContextPath() {
    return window.location.pathname.substring(0,window.location.pathname.indexOf("/", 2)+1);
}

function displayError(messages){
    window.location.replace(getContextPath() + "errorPage.html?errorMSG="+messages.errorMSG+"&errorTitle="+messages.errorTitle);
}

function doCallBack (req, onSuccess, badRequestAction, unauthorizedAction){
    if (req.readyState === XMLHttpRequest.DONE) { // response has arrived
        switch (req.status) {
            case 200:
                onSuccess();
                break;
            case 400: // bad request
                badRequestAction();
                break;
            case 401: // unauthorized
                unauthorizedAction();
                break;
            case 403:
                window.location.replace(getContextPath() + "index.html");
                break;
            default: // server error
                window.location.replace(getContextPath() + "errorPage.html?errorMSG=unknown_error&errorTitle=");
        }
    }
}