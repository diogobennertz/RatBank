import javax.swing.*;
import java.awt.*;

public class BatalhaNavalGUI {

    // Abre uma janela onde o jogador clica no proprio tabuleiro para posicionar cada navio
    static void posicionarFrota(char[][] tab, int jogador, int TAM, int[] navios) {
        JDialog dialog = new JDialog((Frame) null, "Jogador " + jogador + " - posicione sua frota", true);
        int[] idx = {0};               // indice do navio atual em "navios"
        boolean[] horizontal = {true}; // orientacao escolhida no momento

        JLabel info = new JLabel("", SwingConstants.CENTER);
        JButton trocarOrient = new JButton("Orientacao: Horizontal (clique para trocar)");
        JPanel grade = new JPanel(new GridLayout(TAM, TAM));
        JButton[][] botoes = new JButton[TAM][TAM];

        trocarOrient.addActionListener(e -> {
            horizontal[0] = !horizontal[0];
            trocarOrient.setText("Orientacao: " + (horizontal[0] ? "Horizontal" : "Vertical") + " (clique para trocar)");
        });

        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                JButton botao = new JButton();
                int lin = i, col = j;

                botao.addActionListener(e -> {
                    int tam = navios[idx[0]];
                    boolean cabe = true;
                    for (int k = 0; k < tam; k++) {
                        int l = horizontal[0] ? lin : lin + k;
                        int c = horizontal[0] ? col + k : col;
                        if (l < 0 || l >= TAM || c < 0 || c >= TAM || tab[l][c] == 'N') cabe = false;
                    }
                    if (!cabe) {
                        info.setText("Posicao invalida! Escolha outra celula.");
                        return;
                    }
                    for (int k = 0; k < tam; k++) {
                        int l = horizontal[0] ? lin : lin + k;
                        int c = horizontal[0] ? col + k : col;
                        tab[l][c] = 'N';
                        botoes[l][c].setText("🚢");
                        botoes[l][c].setBackground(Color.GREEN);
                    }
                    idx[0]++;
                    if (idx[0] == navios.length) {
                        dialog.dispose(); // ultimo navio posicionado, fecha a janela
                    } else {
                        info.setText("Jogador " + jogador + ": posicione o navio de tamanho " + navios[idx[0]]);
                    }
                });

                botoes[i][j] = botao;
                grade.add(botao);
            }
        }

        info.setText("Jogador " + jogador + ": posicione o navio de tamanho " + navios[0]);
        dialog.setLayout(new BorderLayout());
        dialog.add(info, BorderLayout.NORTH);
        dialog.add(grade, BorderLayout.CENTER);
        dialog.add(trocarOrient, BorderLayout.SOUTH);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true); // bloqueia ate o jogador terminar de posicionar tudo
    }

    public static void main(String[] args) {
        int TAM = 10;
        int[] navios = {4, 3, 2}; // tamanhos dos navios da frota
        int totalNavio = navios[0] + navios[1] + navios[2];

        char[][] tab1 = new char[TAM][TAM];     // navios do Jogador 1
        char[][] tab2 = new char[TAM][TAM];     // navios do Jogador 2
        char[][] ataque1 = new char[TAM][TAM];  // tiros que o Jogador 1 deu no tabuleiro do Jogador 2
        char[][] ataque2 = new char[TAM][TAM];  // tiros que o Jogador 2 deu no tabuleiro do Jogador 1

        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                tab1[i][j] = '~';
                tab2[i][j] = '~';
                ataque1[i][j] = '~';
                ataque2[i][j] = '~';
            }
        }

        // ---------- Jogadores posicionam seus navios (clicando no proprio tabuleiro) ----------
        JOptionPane.showMessageDialog(null, "Jogador 1, posicione sua frota.");
        posicionarFrota(tab1, 1, TAM, navios);

        JOptionPane.showMessageDialog(null, "Passe o computador para o Jogador 2.");
        posicionarFrota(tab2, 2, TAM, navios);

        JOptionPane.showMessageDialog(null, "Frotas posicionadas! Jogador 1 ataca primeiro.");

        // ---------- Estado do jogo ----------
        int[] turno = {1};       // de quem e a vez (1 ou 2)
        int[] acertos1 = {0};    // acertos do Jogador 1 na frota do Jogador 2
        int[] acertos2 = {0};    // acertos do Jogador 2 na frota do Jogador 1

        // ---------- Monta a janela do jogo ----------
        JFrame janela = new JFrame("Batalha Naval - 2 Jogadores");
        JLabel status = new JLabel("Vez do Jogador 1 - atire na frota do Jogador 2", SwingConstants.CENTER);
        JPanel grade = new JPanel(new GridLayout(TAM, TAM));
        JButton[][] botoes = new JButton[TAM][TAM];

        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {
                JButton botao = new JButton("~");
                int lin = i, col = j;

                botao.addActionListener(e -> {
                    char[][] alvoNavios = (turno[0] == 1) ? tab2 : tab1;
                    char[][] alvoAtaque = (turno[0] == 1) ? ataque1 : ataque2;

                    if (alvoAtaque[lin][col] != '~') return; // ja atirou aqui

                    boolean acertou = alvoNavios[lin][col] == 'N';
                    if (acertou) {
                        alvoAtaque[lin][col] = 'X';
                        botao.setText("🚢");
                        botao.setBackground(Color.RED);
                        if (turno[0] == 1) acertos1[0]++; else acertos2[0]++;
                    } else {
                        alvoAtaque[lin][col] = 'O';
                        botao.setText("🌊");
                        botao.setBackground(Color.BLUE);
                    }
                    JOptionPane.showMessageDialog(janela, acertou ? "Acertou um navio!" : "Tiro na agua!");

                    int acertosAtual = (turno[0] == 1) ? acertos1[0] : acertos2[0];
                    if (acertosAtual == totalNavio) {
                        JOptionPane.showMessageDialog(janela, "Jogador " + turno[0] + " venceu! Afundou toda a frota inimiga!");
                        for (JButton[] linhaBotoes : botoes)
                            for (JButton b : linhaBotoes) b.setEnabled(false);
                        return;
                    }

                    // Troca o turno e redesenha o tabuleiro com os tiros do proximo jogador
                    turno[0] = (turno[0] == 1) ? 2 : 1;
                    status.setText("Vez do Jogador " + turno[0] + " - atire na frota do Jogador " + (turno[0] == 1 ? 2 : 1));
                    JOptionPane.showMessageDialog(janela, "Passe o computador para o Jogador " + turno[0]);

                    char[][] novoAtaque = (turno[0] == 1) ? ataque1 : ataque2;
                    for (int x = 0; x < TAM; x++) {
                        for (int y = 0; y < TAM; y++) {
                            char v = novoAtaque[x][y];
                            botoes[x][y].setText(v == 'X' ? "🚢" : (v == 'O' ? "🌊" : "~"));
                            botoes[x][y].setBackground(v == 'X' ? Color.RED : (v == 'O' ? Color.BLUE : null));
                        }
                    }
                });

                botoes[i][j] = botao;
                grade.add(botao);
            }
        }

        janela.setLayout(new BorderLayout());
        janela.add(status, BorderLayout.NORTH);
        janela.add(grade, BorderLayout.CENTER);
        janela.setSize(600, 650);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setVisible(true);
    }
}