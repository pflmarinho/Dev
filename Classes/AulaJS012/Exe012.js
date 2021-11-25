var agora = new Date
var hora = agora.getHours()//hora do sistema do computator
console.log(`agora são exatamente ${hora} horas.`)
if (hora < 12){
    console.log(`bom dia`)
}
else if (hora < 18){//encadeamento multiplas condicoes
    console.log(`boa tarde`)
}
else {
    console.log(`boa noite`)
}