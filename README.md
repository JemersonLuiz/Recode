# Calculadora Recode

Aplicação Java Swing para simular o custo de apostas da Lotofácil, o rateio entre participantes e a chance de acertar os 15 números.

## Executar

Com um JDK 8 ou superior, execute na pasta do projeto.

```sh
javac -encoding UTF-8 CalculadoraRecodeFrame.java Lotofacil.java
java CalculadoraRecodeFrame
```

## Como interpretar os resultados

O cálculo usa o preço de R$ 3,50 por combinação simples e aceita bilhetes de 15 a 20 números, conforme a [Lotofácil da CAIXA](https://loterias.caixa.gov.br/Paginas/LOTOFACIL.aspx). O rateio por pessoa é uma média arredondada ao centavo. A simulação não inclui tarifas nem valida os limites e valores de cotas do bolão oficial.

A chance do grupo estima a probabilidade de pelo menos um bilhete acertar os 15 números quando os bilhetes são escolhidos de forma independente. A chance exata para bilhetes já escolhidos depende de quantas combinações diferentes eles cobrem.
