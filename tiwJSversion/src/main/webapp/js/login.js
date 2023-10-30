{
    let loginForm = document.getElementById("loginForm");
    let submitButton = loginForm.querySelector("input[type='button']");

    window.addEventListener("load", function() {

        let cookie = getCookie("username");
        if(cookie!=null) {
            let formData = new FormData();
            formData.append("userID", cookie);
            HTTPRequest("POST", "loginCookies", formData,function (req){
                if(req.status===200){
                    window.location.replace(getContextPath() + "mainPage.html");
                }
            });
        } else {
            document.getElementById("loaderBackground").style.display = "none";
            document.getElementById("loader").style.display = "none";
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