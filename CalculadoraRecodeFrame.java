import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

public class CalculadoraRecodeFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color FUNDO = new Color(0xEEF4F2);
    private static final Color BRANCO = Color.WHITE;
    private static final Color TEXTO = new Color(0x132E29);
    private static final Color SECUNDARIO = new Color(0x46615B);
    private static final Color BORDA = new Color(0xD5E0DB);
    private static final Color VERDE = new Color(0x14654F);
    private static final Color ESCURO = new Color(0x102F29);
    private static final Color CLARO = new Color(0xDCEEE7);
    private static final Color ERRO = new Color(0xA42738);
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final JComboBox<Integer> numeros = new JComboBox<>();
    private final JComboBox<Integer> pessoas = new JComboBox<>();
    private final JSpinner bilhetes = new JSpinner(new SpinnerNumberModel(1, 1, Lotofacil.MAX_BILHETES, 1));
    private final JTextArea estado = textoQuebravel("", 12, ERRO);
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(PT_BR);
    private final NumberFormat inteiro = NumberFormat.getIntegerInstance(PT_BR);
    private final NumberFormat percentual = NumberFormat.getPercentInstance(PT_BR);

    private final JLabel valorTotal = rotulo("", 29, Font.BOLD, TEXTO);
    private final JLabel valorPessoa = new JLabel() {
        @Override
        public void setBounds(int x, int y, int largura, int altura) {
            super.setBounds(x, y, largura, altura);
            ajustarFonteRateio(this);
        }
    };
    private final JLabel chanceGrupo = rotulo("", 24, Font.BOLD, VERDE);
    private final JLabel chanceBilhete = rotulo("", 24, Font.BOLD, TEXTO);
    private final JLabel combinacoes = rotulo("", 24, Font.BOLD, TEXTO);
    private final DefaultTableModel detalhes = modelo("Combinação e probabilidade", "Valor");

    public CalculadoraRecodeFrame() {
        super("Recode | Calculadora da Lotofácil");
        valorPessoa.setFont(fonte(24, Font.BOLD));
        valorPessoa.setForeground(TEXTO);
        percentual.setMinimumFractionDigits(2);
        percentual.setMaximumFractionDigits(8);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel raiz = plano(new BorderLayout());
        raiz.setBackground(FUNDO);
        raiz.setOpaque(true);
        raiz.add(cabecalho(), BorderLayout.NORTH);

        JPanel corpo = plano(new BorderLayout(18, 12));
        corpo.setBorder(new EmptyBorder(20, 24, 16, 24));
        corpo.add(formulario(), BorderLayout.WEST);
        corpo.add(resultados(), BorderLayout.CENTER);
        JTextArea nota = textoQuebravel("Simulação de apostas e rateio, sem tarifas. "
                + "Limites e valores de cotas do bolão oficial seguem as regras da CAIXA.", 12, SECUNDARIO);
        nota.setPreferredSize(new Dimension(0, 20));
        corpo.add(nota, BorderLayout.SOUTH);
        raiz.add(corpo, BorderLayout.CENTER);
        setContentPane(raiz);

        setMinimumSize(new Dimension(780, 700));
        setSize(1100, 740);
        setLocationRelativeTo(null);
        calcular();
    }

    private JPanel cabecalho() {
        JPanel painel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D desenho = (Graphics2D) graphics.create();
                desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                desenho.setColor(new Color(0x1B463C));
                desenho.fillOval(getWidth() - 243, -136, 370, 315);
                desenho.setColor(new Color(0x265749));
                desenho.drawOval(getWidth() - 174, -105, 302, 260);
                desenho.drawOval(getWidth() - 144, -81, 258, 220);
                desenho.dispose();
            }
        };
        painel.setBackground(ESCURO);
        painel.setBorder(new EmptyBorder(18, 28, 20, 28));
        JPanel textos = coluna();
        textos.add(rotulo("RECODE", 13, Font.BOLD, new Color(0x9DDBC3)));
        textos.add(Box.createVerticalStrut(4));
        textos.add(rotulo("Calculadora da Lotofácil", 34, Font.BOLD, BRANCO));
        textos.add(Box.createVerticalStrut(4));
        textos.add(rotulo("Simule o custo, o rateio e a chance de acertar 15 números.", 15, Font.PLAIN, CLARO));
        painel.add(textos, BorderLayout.WEST);
        return painel;
    }

    private JPanel formulario() {
        JPanel cartao = cartao(new BorderLayout());
        cartao.setPreferredSize(new Dimension(266, 0));
        JPanel campos = coluna();
        campos.add(rotulo("Sua aposta", 23, Font.BOLD, TEXTO));
        campos.add(Box.createVerticalStrut(17));

        for (int i = Lotofacil.MIN_NUMEROS; i <= Lotofacil.MAX_NUMEROS; i++) {
            numeros.addItem(i);
        }
        for (int i = 1; i <= Lotofacil.MAX_PESSOAS; i++) {
            pessoas.addItem(i);
        }
        bilhetes.setLocale(PT_BR);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(bilhetes, "0");
        JFormattedTextField campoBilhetes = editor.getTextField();
        campoBilhetes.setFocusLostBehavior(JFormattedTextField.PERSIST);
        NumberFormatter formatoBilhetes = new NumberFormatter(editor.getFormat()) {
            @Override
            public Object stringToValue(String texto) throws ParseException {
                if (!texto.matches("[0-9]+")) {
                    throw new ParseException("Quantidade inválida", 0);
                }
                try {
                    int quantidade = Integer.parseInt(texto);
                    if (quantidade >= 1 && quantidade <= Lotofacil.MAX_BILHETES) {
                        return quantidade;
                    }
                } catch (NumberFormatException ex) {
                    throw new ParseException("Quantidade inválida", 0);
                }
                throw new ParseException("Quantidade fora dos limites", 0);
            }
        };
        campoBilhetes.setFormatterFactory(new DefaultFormatterFactory(formatoBilhetes));
        campoBilhetes.setHorizontalAlignment(JFormattedTextField.LEFT);
        campoBilhetes.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "calcular");
        campoBilhetes.getActionMap().put("calcular", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                calcular();
            }
        });
        bilhetes.setEditor(editor);

        adicionarCampo(campos, "Números por bilhete", 'N', numeros, numeros);
        adicionarCampo(campos, "Quantidade de bilhetes", 'B', bilhetes, campoBilhetes);
        adicionarCampo(campos, "Pessoas no rateio", 'P', pessoas, pessoas);

        JButton botao = new JButton("Calcular projeção") {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D desenho = (Graphics2D) graphics.create();
                desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                desenho.setColor(getModel().isPressed() ? ESCURO
                        : getModel().isRollover() ? new Color(0x1B765E) : VERDE);
                desenho.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (hasFocus()) {
                    desenho.setColor(BRANCO);
                    desenho.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 7, 9, 9);
                }
                desenho.dispose();
                super.paintComponent(graphics);
            }
        };
        botao.setName("calcular");
        botao.setMnemonic('C');
        botao.setUI(new BasicButtonUI());
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setFont(fonte(15, Font.BOLD));
        botao.setBackground(VERDE);
        botao.setForeground(BRANCO);
        botao.setMargin(new Insets(10, 16, 10, 16));
        botao.setPreferredSize(new Dimension(200, 42));
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.addActionListener(evento -> calcular());
        getRootPane().setDefaultButton(botao);
        campos.add(botao);
        campos.add(Box.createVerticalStrut(7));
        estado.setRows(3);
        estado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        campos.add(estado);
        cartao.add(campos, BorderLayout.NORTH);
        return cartao;
    }

    private void adicionarCampo(JPanel painel, String legenda, char atalho, JComponent campo, JComponent foco) {
        JLabel rotulo = rotulo(legenda, 14, Font.BOLD, TEXTO);
        rotulo.setDisplayedMnemonic(atalho);
        rotulo.setLabelFor(foco);
        painel.add(rotulo);
        painel.add(Box.createVerticalStrut(5));
        campo.setFont(fonte(15, Font.PLAIN));
        campo.setPreferredSize(new Dimension(200, 37));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 37));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(campo);
        painel.add(Box.createVerticalStrut(14));
    }

    private JPanel resultados() {
        JPanel cartao = cartao(new BorderLayout(0, 12));
        cartao.add(rotulo("Resultados", 23, Font.BOLD, TEXTO), BorderLayout.NORTH);
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(fonte(14, Font.PLAIN));
        abas.addTab("Resumo", resumo());
        abas.addTab("Detalhes", tabela(detalhes));
        abas.addTab("Comparações", tabela(comparacoes()));
        cartao.add(abas, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel resumo() {
        JPanel painel = plano(new BorderLayout(0, 12));
        painel.setBorder(new EmptyBorder(12, 8, 8, 8));
        JPanel numeros = plano(new GridLayout(2, 2, 10, 10));
        numeros.add(metrica("Rateio por pessoa", valorPessoa, FUNDO));
        numeros.add(metrica("Chance do grupo (estimada)", chanceGrupo, CLARO));
        numeros.add(metrica("Combinações por bilhete", combinacoes, FUNDO));
        numeros.add(metrica("Chance por bilhete", chanceBilhete, FUNDO));

        JPanel dados = plano(new BorderLayout(0, 10));
        dados.add(metrica("Custo total", valorTotal, CLARO), BorderLayout.NORTH);
        dados.add(numeros, BorderLayout.CENTER);
        painel.add(dados, BorderLayout.CENTER);
        JTextArea explicacao = textoQuebravel("As chances são para 15 acertos. A estimativa do grupo pressupõe "
                + "bilhetes escolhidos de forma independente. Bilhetes repetidos não aumentam a cobertura real.",
                13, SECUNDARIO);
        explicacao.setPreferredSize(new Dimension(0, 51));
        painel.add(explicacao, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel metrica(String titulo, JLabel valor, Color fundo) {
        JPanel painel = new JPanel(new BorderLayout(0, 3));
        painel.setBackground(fundo);
        painel.setBorder(new EmptyBorder(10, 11, 10, 11));
        JLabel legenda = rotulo(titulo, 12, Font.BOLD, SECUNDARIO);
        legenda.setLabelFor(valor);
        painel.add(legenda, BorderLayout.NORTH);
        painel.add(valor, BorderLayout.CENTER);
        return painel;
    }

    private JScrollPane tabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo) {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                int larguraMinima = 0;
                for (int coluna = 0; coluna < getColumnCount(); coluna++) {
                    larguraMinima += getColumnModel().getColumn(coluna).getMinWidth();
                }
                return getParent() != null && getParent().getWidth() >= larguraMinima;
            }
        };
        tabela.setFillsViewportHeight(true);
        tabela.setFont(fonte(14, Font.PLAIN));
        tabela.setRowHeight(34);
        tabela.setGridColor(BORDA);
        tabela.setShowVerticalLines(false);
        tabela.getTableHeader().setFont(fonte(13, Font.BOLD));
        tabela.getTableHeader().setReorderingAllowed(false);
        if (modelo.getColumnCount() == 2) {
            tabela.getColumnModel().getColumn(0).setPreferredWidth(308);
            tabela.getColumnModel().getColumn(1).setPreferredWidth(110);
        } else {
            tabela.getColumnModel().getColumn(0).setPreferredWidth(170);
            tabela.getColumnModel().getColumn(1).setPreferredWidth(303);
            tabela.getColumnModel().getColumn(2).setPreferredWidth(130);
        }
        for (int i = 0; i < modelo.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setMinWidth(
                    tabela.getColumnModel().getColumn(i).getPreferredWidth());
        }
        JScrollPane painel = new JScrollPane(tabela);
        painel.setBorder(new EmptyBorder(10, 6, 6, 6));
        return painel;
    }

    private DefaultTableModel comparacoes() {
        DefaultTableModel modelo = modelo("Lotofácil (1 bilhete)", "Evento de comparação", "Chance do evento");
        String[] eventos = {
                "16 caras seguidas", "Mesmo número específico 4× na roleta europeia",
                "Um número específico 6× no dado", "7 vitórias seguidas no jokenpô",
                "3 empates seguidos com tesoura", "6 vitórias seguidas no jokenpô"
        };
        double[] chances = {
                Math.pow(0.5, 16), Math.pow(1.0 / 37, 4), Math.pow(1.0 / 6, 6),
                Math.pow(1.0 / 3, 7), Math.pow(1.0 / 9, 3), Math.pow(1.0 / 3, 6)
        };
        for (int i = 0; i < eventos.length; i++) {
            int quantidade = Lotofacil.MIN_NUMEROS + i;
            modelo.addRow(new Object[]{
                    quantidade + " números · " + percentual.format(Lotofacil.chancePorBilhete(quantidade)),
                    eventos[i], percentual.format(chances[i])
            });
        }
        return modelo;
    }

    private void calcular() {
        JFormattedTextField campoBilhetes = ((JSpinner.NumberEditor) bilhetes.getEditor()).getTextField();
        try {
            bilhetes.commitEdit();
        } catch (ParseException ex) {
            estado.setText("Use um número inteiro de 1 a " + inteiro.format(Lotofacil.MAX_BILHETES)
                    + ". Os resultados anteriores foram mantidos.");
            campoBilhetes.requestFocusInWindow();
            return;
        }

        Lotofacil.Resultado resultado = Lotofacil.calcular((Integer) numeros.getSelectedItem(),
                (Integer) bilhetes.getValue(), (Integer) pessoas.getSelectedItem());
        estado.setText("");
        valorTotal.setText(moeda.format(resultado.valorTotal));
        valorPessoa.setText(moeda.format(resultado.valorPorPessoa));
        ajustarFonteRateio(valorPessoa);
        chanceGrupo.setText(resultado.chanceGrupo >= 1 - 5e-11
                ? "≈ " + percentual.format(1) : percentual.format(resultado.chanceGrupo));
        chanceBilhete.setText(percentual.format(resultado.chanceBilhete));
        combinacoes.setText(inteiro.format(resultado.combinacoesPorBilhete));
        detalhes.setRowCount(0);
        detalhes.addRow(new Object[]{"Custo por bilhete", moeda.format(resultado.valorBilhete)});
        String chanceSemAcerto = resultado.chanceSemAcerto < 5e-11
                ? "< " + percentual.format(1e-10) : percentual.format(resultado.chanceSemAcerto);
        detalhes.addRow(new Object[]{"Chance do grupo sem 15 acertos (estimada)", chanceSemAcerto});
        detalhes.addRow(new Object[]{"Resultados ainda não cobertos por um bilhete",
                inteiro.format(Lotofacil.TOTAL_RESULTADOS - resultado.combinacoesPorBilhete)});
    }

    private static void ajustarFonteRateio(JLabel valor) {
        int largura = valor.getWidth();
        int tamanho = 24;
        while (largura > 0 && tamanho > 12
                && valor.getFontMetrics(fonte(tamanho, Font.BOLD)).stringWidth(valor.getText()) > largura) {
            tamanho--;
        }
        if (valor.getFont().getSize() != tamanho) {
            valor.setFont(fonte(tamanho, Font.BOLD));
        }
    }

    private static DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    private static JPanel plano(LayoutManager layout) {
        JPanel painel = new JPanel(layout);
        painel.setOpaque(false);
        return painel;
    }

    private static JPanel coluna() {
        JPanel painel = plano(null);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        return painel;
    }

    private static JPanel cartao(BorderLayout layout) {
        JPanel painel = new JPanel(layout);
        painel.setBackground(BRANCO);
        painel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDA),
                new EmptyBorder(16, 16, 14, 16)));
        return painel;
    }

    private static JLabel rotulo(String texto, int tamanho, int estilo, Color cor) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(fonte(tamanho, estilo));
        rotulo.setForeground(cor);
        return rotulo;
    }

    private static JTextArea textoQuebravel(String texto, int tamanho, Color cor) {
        JTextArea area = new JTextArea(texto);
        area.setUI(new BasicTextAreaUI());
        area.setBorder(new EmptyBorder(0, 0, 0, 0));
        area.setFont(fonte(tamanho, Font.PLAIN));
        area.setForeground(cor);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        return area;
    }

    private static Font fonte(int tamanho, int estilo) {
        return new Font("Segoe UI", estilo, tamanho);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("control", FUNDO);
            UIManager.put("nimbusBase", VERDE);
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (javax.swing.UnsupportedLookAndFeelException | ClassNotFoundException
                     | InstantiationException | IllegalAccessException ex) {
                // A aparência padrão do Swing mantém a janela utilizável.
            }
            new CalculadoraRecodeFrame().setVisible(true);
        });
    }
}
