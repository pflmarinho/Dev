const pessoa = {
    nome: "Ana",
    idade:  5,
    endereco:{
        logradouro : "Rua ABC",
        numero: 1000
    }
}

const{nome, idade} = pessoa //destruturing o nome e a idade do array passoa (objeto)
console.log(nome, idade)
console.log(pessoa)