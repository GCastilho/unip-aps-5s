$(() => {
    //configurações da scrollbar
    $('.left-section, .message').mCustomScrollbar({
        scrollInertia: 85,
        callbacks:{
            onTotalScrollBack:function(){
                ws.send(JSON.stringify({
                    command: 'getMessages',
                    receiver,
                    lastID: messageId
                }));
                setTimeout(function () {
                    $('.message').mCustomScrollbar("scrollTo", "#"+lastMessageId,{scrollInertia: 0});
                }, 100)
            }
        }
    });
    $('.left-section').mCustomScrollbar({
        height: 0
    });
});

//função para mudar o posicionamento do scrollbar
let scrollUpdate = () => {
    $(() => {$('.message').mCustomScrollbar("scrollTo", "bottom",{scrollInertia: 0})})
};

