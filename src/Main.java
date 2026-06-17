import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main {

    static int pontos = 0;
    static int tiros = 30;
    static int naviosEncontrados = 0;
    static final int TOTAL_NAVIOS = 10;

    public static void main(String[] args) {

        UIManager.put("Label.font",
                new Font("Segoe UI Emoji", Font.PLAIN, 16));

        UIManager.put("Button.font",
                new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JFrame janela = new JFrame("🚢 Batalha Naval");
        janela.setSize(800, 700);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel(
                "🚢 BATALHA NAVAL 🚢",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        titulo.setForeground(new Color(25, 25, 112));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Informações
        JPanel painelInfo = new JPanel();

        JLabel lblPontos = new JLabel("⭐ Pontos: 0");
        JLabel lblTiros = new JLabel("🎯 Tiros: 30");

        lblPontos.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        lblTiros.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));

        painelInfo.add(lblPontos);
        painelInfo.add(Box.createHorizontalStrut(50));
        painelInfo.add(lblTiros);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(titulo, BorderLayout.NORTH);
        topo.add(painelInfo, BorderLayout.CENTER);

        janela.add(topo, BorderLayout.NORTH);

        // Tabuleiro
        JPanel tabuleiro = new JPanel();
        tabuleiro.setLayout(new GridLayout(10, 10, 2, 2));
        tabuleiro.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        boolean[][] navios = new boolean[10][10];
        JButton[][] botoes = new JButton[10][10];

        Random random = new Random();

        int quantidade = 0;

        while (quantidade < TOTAL_NAVIOS) {

            int linha = random.nextInt(10);
            int coluna = random.nextInt(10);

            if (!navios[linha][coluna]) {
                navios[linha][coluna] = true;
                quantidade++;
            }
        }

        for (int linha = 0; linha < 10; linha++) {

            for (int coluna = 0; coluna < 10; coluna++) {

                JButton botao = new JButton("🌊");

                botao.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                botao.setBackground(new Color(70, 130, 180));
                botao.setForeground(Color.WHITE);
                botao.setFocusPainted(false);

                botoes[linha][coluna] = botao;

                int l = linha;
                int c = coluna;

                botao.addActionListener(e -> {

                    if (!botao.isEnabled()) {
                        return;
                    }

                    tiros--;

                    if (navios[l][c]) {

                        botao.setText("💥");
                        botao.setBackground(new Color(220, 20, 60));

                        pontos += 100;
                        naviosEncontrados++;

                    } else {

                        botao.setText("💦");
                        botao.setBackground(new Color(0, 102, 167));
                    }

                    botao.setEnabled(false);

                    lblPontos.setText("⭐ Pontos: " + pontos);
                    lblTiros.setText("🎯 Tiros: " + tiros);

                    // Vitória
                    if (naviosEncontrados == TOTAL_NAVIOS) {

                        JOptionPane.showMessageDialog(
                                null,
                                "🏆 Parabéns!\nVocê encontrou todos os navios!"
                        );

                        for (int i = 0; i < 10; i++) {
                            for (int j = 0; j < 10; j++) {
                                botoes[i][j].setEnabled(false);
                            }
                        }
                    }

                    // Derrota
                    if (tiros == 0 && naviosEncontrados < TOTAL_NAVIOS) {

                        JOptionPane.showMessageDialog(
                                null,
                                "💀 Fim de jogo!\nPontuação: " + pontos
                        );

                        for (int i = 0; i < 10; i++) {
                            for (int j = 0; j < 10; j++) {
                                botoes[i][j].setEnabled(false);
                            }
                        }
                    }
                });

                tabuleiro.add(botao);
            }
        }

        JButton reiniciar = new JButton("🔄 Novo Jogo");
        reiniciar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));

        reiniciar.addActionListener(e -> {
            janela.dispose();
            pontos = 0;
            tiros = 30;
            naviosEncontrados = 0;
            main(null);
        });

        JPanel rodape = new JPanel();
        rodape.add(reiniciar);

        janela.add(tabuleiro, BorderLayout.CENTER);
        janela.add(rodape, BorderLayout.SOUTH);

        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }
}