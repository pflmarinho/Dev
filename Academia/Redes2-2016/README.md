# Redes de computadores 2

Chat desenvolvido em java com mensagens trocadas entre clientes e servidor criptografadas utilizando RSA.

## Autor: 
- Mário Igor .

## Linguagem de programação: 
- Java.

## Protocolo de criptografia:
- RSA.

## Instruções para execução: 
- Abrir projeto (preferencialmente no eclipse).
- Executar as classes "Cliente" e "Servidor" do pacote "telas".

## Informações adicionais: 
- Toda a lógica de implementação do protocolo de criptografia RSA encontra-se na classe "RSA" do pacote "util".
- A classe "RSA" permite a encriptação e decriptação de textos de quaisquer tamanhos.
- A troca de chaves entre clientes e servidores acontece através da troca de Strings que representam as chaves (codificadas em Base64).
- Todo o JSON que representa as mensagens trocadas entre cliente e servidor são encriptados com RSA e enviados como String codificadas em Base64.
- A implementação da criptografia abrange TODAS as mensagens trocadas entre clientes e servidor, com uma única exceção para a mensagem inicial de conexão enviada pelo cliente, onde é enviada para o servidor a chave pública do cliente.
- Analisando os logs que são escritos no console por cada uma das classes “Cliente” e “Servidor”, é possível visualizar as mensagens criptografadas e descriptografadas que são trocadas entre o cliente e o servidor.