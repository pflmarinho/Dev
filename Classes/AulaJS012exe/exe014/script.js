function carregar(){
    var msg = window.document.getElementById('msg')
    var img = window.document.getElementById('imagem')
    var data = new Date()   
    var hora = data.getHours()
    //var hora = 18
    msg.innerHTML = `Agora são ${hora} horas.`
    if (hora >= 0 && hora < 12){
        //Bom Dia
        
    }else if (hora >= 12 && hora < 18){
        //Boa Tarde
    }else {
        //Boa Noite

    }
}   