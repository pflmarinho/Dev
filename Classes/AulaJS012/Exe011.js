var idade = 15 
console.log(`você tem ${idade} anos.`)       
if(idade < 16){//aninhamento simples
    console.log(`nao vota`)// ultiliza o node.js
}else if(idade < 18||idade > 65){ //aninhamento composto
    console.log(`voto opicional`)
}else{
    console.log('voto obrigatório')
}