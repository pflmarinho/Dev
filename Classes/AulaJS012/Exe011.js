var idade = 15 
console.log(`você tem ${idade} anos.`)       
if(idade < 16){
    console.log(`nao vota`)// ultiliza o node.js
}else if(idade < 18||idade > 65){ //aninhamento
    console.log(`voto opicional`)
}else{
    console.log('voto obrigatorio')
}