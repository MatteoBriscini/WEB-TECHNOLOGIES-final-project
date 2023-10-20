{
    let loginForm = document.getElementById("loginForm");
    let submitButton = loginForm.querySelector("input[type='button']");

    window.addEventListener("load", function() {

        let cookie = getCookie("username");
        if(cookie!=null) {
            let formData = new FormData();
            formData.append("userID", cookie);
            HTTPRequest("POST", "loginCookies", formData,function (req){
                if(req.status===200)window.location.replace(getContextPath() + "mainPage.html");
            });
        }

        submitButton.addEventListener("click", function(e) {
            new LoginForm(e.target.closest("form")).sendForm("login");
        });
        loginForm.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') new LoginForm(e.target.closest("form")).sendForm("login");
        });
    }, false);

    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
    }

    function gotToSignIN(){
        window.location.replace(getContextPath() + "account.html");
    }

}

class LoginForm{
    #loginForm;
    constructor(_loginForm){
        this.loginForm = _loginForm;
    }


    validate(){
        let inputs = document.getElementsByTagName('input');
        for (let i = 0; i< inputs.length; ++i) {
            if((inputs[i].type==="text" || inputs[i].type==="password")&&inputs[i].value==="")return false;
        }
        return true;
    }

    sendForm(url){
        if(!this.validate()) {
            alert("please fill all the fields");
            return;
        }

        doPost(url,this.loginForm,function(req) { // callback of the POST HTTP request
            doCallBack(req,
                ()=>{window.location.replace(getContextPath() + "mainPage.html")},
                ()=>{alert(req.responseText)},
                ()=>{alert(req.responseText)}
            )}
            ,false);
    }
}