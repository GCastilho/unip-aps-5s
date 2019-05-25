
//Verifica existencia dos cookies e redireciona para /app
window.onload = () => {
    if (localStorage.getItem('key') != null) {
        window.location.replace(window.location + "app");

    }
}