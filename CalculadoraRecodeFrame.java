import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;

public class CalculadoraRecodeFrame extends JFrame {

    private static final long TOTAL_COMBINACOES_LOTOFACIL = combination(25, 15);
    private static final double PRECO_JOGO_15 = 3.50;

    private JComboBox<Integer> cbNumeros;
    private JComboBox<Integer> cbPessoas;
    private JSpinner spBilhetes;
    private JTextArea txtResultado;

    private JTable tabelaCuriosidades;
    private DefaultTableModel curiosidadesModel;

    private JTable tabelaEstatisticas;
    private DefaultTableModel estatisticasModel;

    public CalculadoraRecodeFrame() {
        super("Calculadora Recode");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // === FONTES GRANDES E ESTÁVEIS ===
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 24));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 32));
        UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 24));
        UIManager.put("TextArea.font", new Font("Consolas", Font.PLAIN, 26));
        UIManager.put("Spinner.font", new Font("Segoe UI", Font.PLAIN, 24));
        UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.BOLD, 26));

        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        // ===== HEADER =====
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(102, 0, 255),
                        getWidth(), getHeight(), new Color(0, 128, 255)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        header.setPreferredSize(new Dimension(0, 150));
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblTitulo = new JLabel("Calculadora Recode");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 48));

        JLabel lblSubtitulo = new JLabel("Probabilidade • Valor da Aposta • Simulações de Bolão");
        lblSubtitulo.setForeground(new Color(240, 240, 255));
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 32));

        header.add(lblTitulo, BorderLayout.NORTH);
        header.add(lblSubtitulo, BorderLayout.SOUTH);

        root.add(header, BorderLayout.NORTH);

        // ===== CENTRO =====
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel cardParametros = criarCard();
        cardParametros.setPreferredSize(new Dimension(480, 0));
        cardParametros.add(criarPainelParametros(), BorderLayout.CENTER);

        JPanel cardResultado = criarCard();
        cardResultado.add(criarPainelResultado(), BorderLayout.CENTER);

        center.add(cardParametros, BorderLayout.WEST);
        center.add(cardResultado, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
    }

    private JPanel criarCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(160, 160, 160), 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }

    private JPanel criarPainelParametros() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel titulo = new JLabel("Parâmetros da Aposta");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titulo, gbc);

        gbc.gridy++;
        panel.add(new JLabel("Números escolhidos (15 a 25):"), gbc);

        cbNumeros = new JComboBox<>();
        for (int i = 15; i <= 25; i++) cbNumeros.addItem(i);
        estilizarCampo(cbNumeros);
        gbc.gridy++;
        panel.add(cbNumeros, gbc);

        gbc.gridy++;
        panel.add(new JLabel("Pessoas no bolão (1 a 40):"), gbc);

        cbPessoas = new JComboBox<>();
        for (int i = 1; i <= 40; i++) cbPessoas.addItem(i);
        estilizarCampo(cbPessoas);
        gbc.gridy++;
        panel.add(cbPessoas, gbc);

        gbc.gridy++;
        panel.add(new JLabel("Quantidade de bilhetes:"), gbc);

        spBilhetes = new JSpinner(new SpinnerNumberModel(1, 1, 9_999_999, 1));
        estilizarCampo(spBilhetes);
        gbc.gridy++;
        panel.add(spBilhetes, gbc);

        gbc.gridy++;
        JButton btnCalcular = criarBotaoModerno("CALCULAR");
        btnCalcular.addActionListener(e -> calcular());
        panel.add(btnCalcular, gbc);

        return panel;
    }

    private JPanel criarPainelResultado() {
        JPanel panel = new JPanel(new BorderLayout());

        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setBackground(new Color(245, 247, 250));
        txtResultado.setFont(new Font("Consolas", Font.PLAIN, 26));
        txtResultado.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollResultados = new JScrollPane(txtResultado);

        curiosidadesModel = new DefaultTableModel(new Object[]{"Curiosidade", "Valor"}, 0);
        tabelaCuriosidades = new JTable(curiosidadesModel);
        estilizarTabela(tabelaCuriosidades);

        estatisticasModel = new DefaultTableModel(
                new Object[]{"Evento", "Evento Aproximado", "Probabilidade"}, 0
        );
        tabelaEstatisticas = new JTable(estatisticasModel);
        estilizarTabela(tabelaEstatisticas);

        JScrollPane scrollCuriosidades = new JScrollPane(tabelaCuriosidades);
        JScrollPane scrollEstatisticas = new JScrollPane(tabelaEstatisticas);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 28));
        tabs.addTab("Resultados", scrollResultados);
        tabs.addTab("Curiosidades", scrollCuriosidades);
        tabs.addTab("Estatísticas", scrollEstatisticas);

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private void estilizarTabela(JTable tabela) {
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 26));
        tabela.setRowHeight(42);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 30));
    }

    private JButton criarBotaoModerno(String texto) {
        JButton btn = new JButton(texto);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(70, 0, 180));
        btn.setBorder(new EmptyBorder(20, 40, 20, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 32));
        return btn;
    }

    private void estilizarCampo(JComponent cmp) {
        cmp.setBorder(new LineBorder(Color.GRAY, 2, true));
        cmp.setPreferredSize(new Dimension(280, 50));
    }

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
