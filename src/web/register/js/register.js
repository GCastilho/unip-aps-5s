function register() {
    var usr = document.getElementById("inputUser").value;
    var pas = document.getElementById("inputPassword").value;

    if(usr != undefined && usr != null && usr != "" &&
        pas != undefined && pas != null && pas != ""){

        $.post(document.location + "",JSON.stringify({user: usr,password:pas}),(result)=>{
            console.log(result);
        });

    }else{
        alert("Favor preencher todos os campos");
    }

}