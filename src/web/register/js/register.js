function register() {
    var usr = document.getElementById("inputUser").value;
    var pas = document.getElementById("inputPassword").value;

    if(usr != undefined && usr != null && usr != "" &&
        pas != undefined && pas != null && pas != ""){

        $.post(document.location + "",JSON.stringify({user: usr,password:pas}),(result)=>{
            try{
                var data =JSON.parse(result);
                console.log(data);
                if(data.message != undefined){
                    alert(data.message);
                }
            } catch(e){
                console.log("Error on parsing json:\n" + result + "\n"+e);
            }
        });

    }else{
        alert("Favor preencher todos os campos");
    }

}