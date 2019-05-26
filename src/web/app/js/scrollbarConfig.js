$(() => {
    //configurações da scrollbar
    $('.left-section, .message').mCustomScrollbar({
        scrollInertia: 85
    });
});

//função para mudar o posicionamento do scrollbar
let scrollUpdate = () => {
    $(() => {$('.message').mCustomScrollbar("scrollTo", "bottom",{scrollInertia: 0})})
};

