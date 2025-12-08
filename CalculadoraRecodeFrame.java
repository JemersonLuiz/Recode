
    private void calcular() {
        try {
            int numeros = (Integer) cbNumeros.getSelectedItem();
            int pessoas = (Integer) cbPessoas.getSelectedItem();
            int bilhetes = (Integer) spBilhetes.getValue();

            long combinacoes = combination(numeros, 15);
            double valorAposta = combinacoes * PRECO_JOGO_15;
            double valorTotalBolao = valorAposta * bilhetes;
            double valorPorPessoa = valorTotalBolao / pessoas;

            double probUmBilhete = (double) combinacoes / TOTAL_COMBINACOES_LOTOFACIL;
            double probGrupo = 1 - Math.pow(1 - probUmBilhete, bilhetes);

            DecimalFormat dfMoney = new DecimalFormat("R$ #,##0.00");
            DecimalFormat dfPercent = new DecimalFormat("#,##0.00000");

            StringBuilder sb = new StringBuilder();
            sb.append("Números por bilhete: ").append(numeros).append("\n");
            sb.append("Bilhetes: ").append(bilhetes).append("\n");
            sb.append("Pessoas no bolão: ").append(pessoas).append("\n\n");
            sb.append("Combinações: ").append(String.format("%,d", combinacoes)).append("\n");
            sb.append("Valor total do bolão: ").append(dfMoney.format(valorTotalBolao)).append("\n");
            sb.append("Valor por pessoa: ").append(dfMoney.format(valorPorPessoa)).append("\n\n");
            sb.append("Probabilidade (1 bilhete): ").append(dfPercent.format(probUmBilhete * 100)).append("%\n");
            sb.append("Probabilidade (grupo): ").append(dfPercent.format(probGrupo * 100)).append("%\n");

            txtResultado.setText(sb.toString());

            // ---------- CURIOSIDADES ----------
            curiosidadesModel.setRowCount(0);
            curiosidadesModel.addRow(new Object[]{"Cobertura total", dfPercent.format(probUmBilhete * 100) + "%"});
            curiosidadesModel.addRow(new Object[]{"Prob. de não ganhar", dfPercent.format((1 - probGrupo) * 100) + "%"});
            curiosidadesModel.addRow(new Object[]{"Combinações restantes", TOTAL_COMBINACOES_LOTOFACIL - combinacoes});

            // ---------- ESTATÍSTICAS (6 LINHAS FIXAS) ----------
            estatisticasModel.setRowCount(0);

            estatisticasModel.addRow(new Object[]{
                    "Probabilidade (15 números): 0,00003%",
                    "Cair cara 16 vezes seguidas",
                    "0,000015%"
            });

            estatisticasModel.addRow(new Object[]{
                    "Probabilidade (16 números): 0,00049%",
                    "Cair número específico 4x seguidas na roleta",
                    "0,000146%"
            });

            estatisticasModel.addRow(new Object[]{
                    "Probabilidade (17 números): 0,00416%",
                    "Dado cair o número 2 seis vezes consecutivas",
                    "0,00002143%"
            });

            estatisticasModel.addRow(new Object[]{
                    "Probabilidade (18 números): 0,02496%",
                    "Ganhar 7 rodadas seguidas no Jokenpô",
                    "0,01524%"
            });

            estatisticasModel.addRow(new Object[]{
                    "Probabilidade (19 números): 0,11858%",
                    "Três rodadas seguidas empatando com TESOURA",
                    "0,137%."
            });

            estatisticasModel.addRow(new Object[]{
                    "Probabilidade (20 números): 0,47431%",
                    "Ganhar 6 rodadas seguidas no Jokenpô",
                    "0,137%"
            });

        } catch (Exception ex) {
            txtResultado.setText("Erro ao calcular.");
        }
    }

    private static long combination(int n, int k) {
        if (k > n) return 0;
        if (k > n - k) k = n - k;
        long r = 1;
        for (int i = 1; i <= k; i++) {
            r = r * (n - k + i) / i;
        }
        return r;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculadoraRecodeFrame().setVisible(true));
    }
}

