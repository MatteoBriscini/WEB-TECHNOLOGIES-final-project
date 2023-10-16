{
    let signInForm = document.getElementById("signInForm");
    let submitButton = signInForm.querySelector("input[type='button']");

    window.addEventListener("load", function() {
        submitButton.addEventListener("click", function(e) {
            new LoginForm(e.target.closest("form")).sendForm("signIn");
        });
        signInForm.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') new LoginForm(e.target.closest("form")).sendForm("signIn");
        });
    }, false);
}
