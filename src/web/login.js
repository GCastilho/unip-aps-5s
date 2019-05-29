
/*window.onload = () => {
//Verifica existencia dos cookies e redireciona para /app
    if (document.cookie != null) {
        window.location.replace(window.location + "app");
    }
};//*/

function saveUsername() {
    localStorage.setItem("username", document.getElementById('inputUser').value);
}