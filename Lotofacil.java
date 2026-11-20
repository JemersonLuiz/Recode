import java.math.BigDecimal;
import java.math.RoundingMode;

final class Lotofacil {
    static final int MIN_NUMEROS = 15;
    static final int MAX_NUMEROS = 20;
    static final int MAX_BILHETES = 9_999_999;
    static final int MAX_PESSOAS = 40;
    static final long TOTAL_RESULTADOS = combinacoes(25, 15);
    private static final BigDecimal PRECO_APOSTA_SIMPLES = new BigDecimal("3.50");

    private Lotofacil() {
    }

    static Resultado calcular(int numeros, int bilhetes, int pessoas) {
        if (numeros < MIN_NUMEROS || numeros > MAX_NUMEROS
                || bilhetes < 1 || bilhetes > MAX_BILHETES
                || pessoas < 1 || pessoas > MAX_PESSOAS) {
            throw new IllegalArgumentException("Parâmetros da simulação fora dos limites.");
        }

        long combinacoesPorBilhete = combinacoes(numeros, 15);
        BigDecimal valorBilhete = PRECO_APOSTA_SIMPLES.multiply(BigDecimal.valueOf(combinacoesPorBilhete));
        BigDecimal valorTotal = valorBilhete.multiply(BigDecimal.valueOf(bilhetes));
        BigDecimal valorPorPessoa = valorTotal.divide(BigDecimal.valueOf(pessoas), 2, RoundingMode.HALF_UP);
        double chanceBilhete = chancePorBilhete(numeros);
        double logSemAcerto = bilhetes * Math.log1p(-chanceBilhete);
        double chanceGrupo = -Math.expm1(logSemAcerto);
        return new Resultado(combinacoesPorBilhete, valorBilhete, valorTotal, valorPorPessoa,
                chanceBilhete, chanceGrupo, Math.exp(logSemAcerto));
    }

    static double chancePorBilhete(int numeros) {
        return (double) combinacoes(numeros, 15) / TOTAL_RESULTADOS;
    }

    private static long combinacoes(int n, int k) {
        k = Math.min(k, n - k);
        long resultado = 1;
        for (int i = 1; i <= k; i++) {
            resultado = resultado * (n - k + i) / i;
        }
        return resultado;
    }

    static final class Resultado {
        final long combinacoesPorBilhete;
        final BigDecimal valorBilhete;
        final BigDecimal valorTotal;
        final BigDecimal valorPorPessoa;
        final double chanceBilhete;
        final double chanceGrupo;
        final double chanceSemAcerto;

        Resultado(long combinacoesPorBilhete, BigDecimal valorBilhete, BigDecimal valorTotal,
                  BigDecimal valorPorPessoa, double chanceBilhete, double chanceGrupo, double chanceSemAcerto) {
            this.combinacoesPorBilhete = combinacoesPorBilhete;
            this.valorBilhete = valorBilhete;
            this.valorTotal = valorTotal;
            this.valorPorPessoa = valorPorPessoa;
            this.chanceBilhete = chanceBilhete;
            this.chanceGrupo = chanceGrupo;
            this.chanceSemAcerto = chanceSemAcerto;
        }
    }
}
