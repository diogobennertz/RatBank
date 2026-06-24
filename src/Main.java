import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main {

    // ── Variáveis globais do jogo ─────────────────────────────────────────────
    static int pontosJ1 = 0;
    static int pontosJ2 = 0;
    static int tirosJ1  = 30;
    static int tirosJ2  = 30;
    static int acertosJ1 = 0; // quantos navios do J2 o J1 acertou
    static int acertosJ2 = 0; // quantos navios do J1 o J2 acertou
    static final int TOTAL_NAVIOS = 14; // soma dos tamanhos: 4+3+3+2+2
    static final int[] TAMANHOS   = {4, 3, 3, 2, 2};
    static final String[] NOMES   = {"Porta-Aviões (4)", "Encouraçado (3)", "Cruzador (3)", "Submarino (2)", "Destruidor (2)"};

    // ── Matrizes dos dois jogadores ───────────────────────────────────────────
    static boolean[][] naviosJ1  = new boolean[10][10]; // onde J1 colocou os navios
    static boolean[][] naviosJ2  = new boolean[10][10]; // onde J2 colocou os navios
    static boolean[][] atacadoJ1 = new boolean[10][10]; // posições que J2 já atirou em J1
    static boolean[][] atacadoJ2 = new boolean[10][10]; // posições que J1 já atirou em J2

    // ── Botões dos tabuleiros ─────────────────────────────────────────────────
    static JButton[][] botoesJ1 = new JButton[10][10]; // tabuleiro do J1 (J2 ataca aqui)
    static JButton[][] botoesJ2 = new JButton[10][10]; // tabuleiro do J2 (J1 ataca aqui)

    // ── Controle de fase e turno ──────────────────────────────────────────────
    // faseSetup: 0 = J1 posicionando, 1 = J2 posicionando, 2 = batalha
    static int faseSetup   = 0;
    static int navioAtual  = 0;    // qual navio está sendo posicionado
    static boolean horizontal = true;

    // turno: 1 = vez do J1 atacar, 2 = vez do J2 atacar
    static int turno = 1;

    // ── Componentes da tela ───────────────────────────────────────────────────
    static JLabel lblStatus;
    static JLabel lblTurno;
    static JLabel lblPontosJ1;
    static JLabel lblPontosJ2;
    static JLabel lblTirosJ1;
    static JLabel lblTirosJ2;
    static JButton btnDirecao;

    // ── Painel central que troca de tela ─────────────────────────────────────
    static JPanel painelCentral;
    static CardLayout cardLayout;

    // Telas
    static final String TELA_SETUP_J1  = "setup_j1";
    static final String TELA_SETUP_J2  = "setup_j2";
    static final String TELA_AVISO     = "aviso";
    static final String TELA_BATALHA   = "batalha";

    // Janela principal
    static JFrame janela;

    // ── Mensagem de aviso (passagem de vez) ───────────────────────────────────
    static JLabel lblAviso;

    public static void main(String[] args) {

        UIManager.put("Label.font",  new Font("Segoe UI Emoji", Font.PLAIN, 16));
        UIManager.put("Button.font", new Font("Segoe UI Emoji", Font.PLAIN, 16));

        janela = new JFrame("🚢 Batalha Naval - 2 Jogadores");
        janela.setSize(700, 750);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLayout(new BorderLayout());

        // ── Topo: título + placares ───────────────────────────────────────────
        JPanel topo = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("🚢 BATALHA NAVAL 🚢", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
        titulo.setForeground(new Color(25, 25, 112));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        topo.add(titulo, BorderLayout.NORTH);

        // Placar dos dois jogadores
        JPanel painelPlacar = new JPanel(new GridLayout(1, 2, 10, 0));
        painelPlacar.setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 20));

        JPanel infoJ1 = new JPanel(new GridLayout(2, 1));
        infoJ1.setBackground(new Color(200, 220, 255));
        infoJ1.setBorder(BorderFactory.createLineBorder(new Color(50, 100, 200), 2));
        lblPontosJ1 = new JLabel("⭐ J1: 0 pts", SwingConstants.CENTER);
        lblTirosJ1  = new JLabel("🎯 Tiros: 30", SwingConstants.CENTER);
        lblPontosJ1.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        lblTirosJ1 .setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        infoJ1.add(lblPontosJ1);
        infoJ1.add(lblTirosJ1);

        JPanel infoJ2 = new JPanel(new GridLayout(2, 1));
        infoJ2.setBackground(new Color(255, 210, 210));
        infoJ2.setBorder(BorderFactory.createLineBorder(new Color(200, 50, 50), 2));
        lblPontosJ2 = new JLabel("⭐ J2: 0 pts", SwingConstants.CENTER);
        lblTirosJ2  = new JLabel("🎯 Tiros: 30", SwingConstants.CENTER);
        lblPontosJ2.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        lblTirosJ2 .setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        infoJ2.add(lblPontosJ2);
        infoJ2.add(lblTirosJ2);

        painelPlacar.add(infoJ1);
        painelPlacar.add(infoJ2);
        topo.add(painelPlacar, BorderLayout.CENTER);

        // Turno atual
        lblTurno = new JLabel("🎮 Vez de: Jogador 1 — Posicione seus navios", SwingConstants.CENTER);
        lblTurno.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        lblTurno.setForeground(new Color(0, 80, 160));
        lblTurno.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        topo.add(lblTurno, BorderLayout.SOUTH);

        janela.add(topo, BorderLayout.NORTH);

        // ── Painel central com CardLayout (troca de tela) ─────────────────────
        cardLayout   = new CardLayout();
        painelCentral = new JPanel(cardLayout);

        painelCentral.add(criarTelaSetup(1),  TELA_SETUP_J1);
        painelCentral.add(criarTelaSetup(2),  TELA_SETUP_J2);
        painelCentral.add(criarTelaAviso(),   TELA_AVISO);
        painelCentral.add(criarTelaBatalha(), TELA_BATALHA);

        janela.add(painelCentral, BorderLayout.CENTER);

        // ── Rodapé ────────────────────────────────────────────────────────────
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));

        btnDirecao = new JButton("↔ Horizontal");
        btnDirecao.setBackground(new Color(50, 100, 170));
        btnDirecao.setForeground(Color.WHITE);
        btnDirecao.setFocusPainted(false);
        btnDirecao.addActionListener(e -> {
            horizontal = !horizontal;
            btnDirecao.setText(horizontal ? "↔ Horizontal" : "↕ Vertical");
        });

        JButton btnAuto = new JButton("🎲 Auto-posicionar");
        btnAuto.setBackground(new Color(60, 120, 60));
        btnAuto.setForeground(Color.WHITE);
        btnAuto.setFocusPainted(false);
        btnAuto.addActionListener(e -> autoPosicionar());

        JButton btnNovo = new JButton("🔄 Novo Jogo");
        btnNovo.setBackground(new Color(150, 40, 40));
        btnNovo.setForeground(Color.WHITE);
        btnNovo.setFocusPainted(false);
        btnNovo.addActionListener(e -> resetar());

        lblStatus = new JLabel("Posicione: " + NOMES[0], SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        lblStatus.setForeground(new Color(0, 60, 120));

        rodape.add(btnDirecao);
        rodape.add(btnAuto);
        rodape.add(btnNovo);

        JPanel rodapeTotal = new JPanel(new BorderLayout());
        rodapeTotal.add(lblStatus, BorderLayout.NORTH);
        rodapeTotal.add(rodape,    BorderLayout.CENTER);
        janela.add(rodapeTotal, BorderLayout.SOUTH);

        // Começa mostrando o setup do J1
        cardLayout.show(painelCentral, TELA_SETUP_J1);

        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }

    // =========================================================================
    // TELA DE SETUP — tabuleiro onde o jogador posiciona seus navios
    // =========================================================================
    static JPanel criarTelaSetup(int jogador) {

        JPanel painel = new JPanel(new BorderLayout(6, 6));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80));

        JLabel label = new JLabel(
                jogador == 1 ? "🔵 Jogador 1 — Posicione seus navios" : "🔴 Jogador 2 — Posicione seus navios",
                SwingConstants.CENTER
        );
        label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        label.setForeground(jogador == 1 ? new Color(0, 80, 180) : new Color(180, 0, 0));
        painel.add(label, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(10, 10, 3, 3));

        JButton[][] grade = jogador == 1 ? botoesJ1 : botoesJ2;

        for (int linha = 0; linha < 10; linha++) {
            for (int coluna = 0; coluna < 10; coluna++) {

                JButton btn = new JButton("🌊");
                btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                btn.setBackground(new Color(70, 130, 180));
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);

                grade[linha][coluna] = btn;

                int l = linha, c = coluna;

                // Chama o método global colocarNavio (definido mais abaixo)
                btn.addActionListener(e -> colocarNavio(l, c, jogador));

                grid.add(btn);
            }
        }

        painel.add(grid, BorderLayout.CENTER);
        return painel;
    }

    // =========================================================================
    // TELA DE AVISO — aparece entre os turnos para o outro jogador assumir
    // =========================================================================
    static JPanel criarTelaAviso() {

        JPanel painel = new JPanel(new GridBagLayout()); // centraliza tudo
        painel.setBackground(new Color(20, 20, 60));

        JPanel caixa = new JPanel(new GridLayout(4, 1, 10, 10));
        caixa.setBackground(new Color(30, 30, 80));
        caixa.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 3));
        caixa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 255), 3),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        JLabel icone = new JLabel("🔒", SwingConstants.CENTER);
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));

        lblAviso = new JLabel("", SwingConstants.CENTER);
        lblAviso.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        lblAviso.setForeground(Color.WHITE);

        JLabel instrucao = new JLabel("Passe o computador para o outro jogador.", SwingConstants.CENTER);
        instrucao.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        instrucao.setForeground(new Color(180, 210, 255));

        JButton btnContinuar = new JButton("✅ Estou pronto — Continuar");
        btnContinuar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        btnContinuar.setBackground(new Color(40, 120, 40));
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setFocusPainted(false);
        btnContinuar.addActionListener(e -> continuarAposAviso());

        caixa.add(icone);
        caixa.add(lblAviso);
        caixa.add(instrucao);
        caixa.add(btnContinuar);

        painel.add(caixa);
        return painel;
    }

    // =========================================================================
    // TELA DE BATALHA — dois tabuleiros, cada um mostra apenas seus próprios navios
    // =========================================================================
    static JPanel criarTelaBatalha() {

        JPanel painel = new JPanel(new GridLayout(1, 2, 15, 0));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabuleiro do J1 (J2 ataca aqui — os navios de J1 ficam ocultos para J2)
        JPanel painelJ1 = new JPanel(new BorderLayout(4, 4));
        JLabel lblJ1 = new JLabel("🔵 Tabuleiro J1", SwingConstants.CENTER);
        lblJ1.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        painelJ1.add(lblJ1, BorderLayout.NORTH);

        JPanel gridJ1 = new JPanel(new GridLayout(10, 10, 2, 2));
        for (int l = 0; l < 10; l++) {
            for (int c = 0; c < 10; c++) {
                // Recria os botões do J1 para a tela de batalha
                JButton btn = new JButton("🌊");
                btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
                btn.setBackground(new Color(70, 130, 180));
                btn.setFocusPainted(false);

                botoesJ1[l][c] = btn;

                int linha = l, col = c;
                // J2 ataca o tabuleiro de J1 clicando aqui
                btn.addActionListener(e -> atirar(linha, col, 2)); // quem atira = J2

                gridJ1.add(btn);
            }
        }
        painelJ1.add(gridJ1, BorderLayout.CENTER);
        painel.add(painelJ1);

        // Tabuleiro do J2 (J1 ataca aqui)
        JPanel painelJ2 = new JPanel(new BorderLayout(4, 4));
        JLabel lblJ2 = new JLabel("🔴 Tabuleiro J2", SwingConstants.CENTER);
        lblJ2.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        painelJ2.add(lblJ2, BorderLayout.NORTH);

        JPanel gridJ2 = new JPanel(new GridLayout(10, 10, 2, 2));
        for (int l = 0; l < 10; l++) {
            for (int c = 0; c < 10; c++) {
                JButton btn = new JButton("🌊");
                btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
                btn.setBackground(new Color(70, 130, 180));
                btn.setFocusPainted(false);

                botoesJ2[l][c] = btn;

                int linha = l, col = c;
                // J1 ataca o tabuleiro de J2 clicando aqui
                btn.addActionListener(e -> atirar(linha, col, 1)); // quem atira = J1

                gridJ2.add(btn);
            }
        }
        painelJ2.add(gridJ2, BorderLayout.CENTER);
        painel.add(painelJ2);

        return painel;
    }

    // =========================================================================
    // CONTINUAR APÓS AVISO — botão "Estou pronto" na tela de aviso
    // =========================================================================
    static void continuarAposAviso() {

        if (faseSetup == 1) {
            // J2 vai posicionar agora
            lblStatus.setText("Posicione: " + NOMES[0]);
            lblTurno.setText("🎮 Vez de: Jogador 2 — Posicione seus navios");
            cardLayout.show(painelCentral, TELA_SETUP_J2);

        } else if (faseSetup == 2 && turno == 1) {
            // Batalha começa com J1 atacando
            iniciarBatalha();

        } else {
            // Troca de turno durante a batalha
            mostrarTelaBatalha();
        }
    }

    // =========================================================================
    // INICIAR BATALHA — prepara os tabuleiros e mostra a tela de batalha
    // =========================================================================
    static void iniciarBatalha() {
        // Recria os botões da tela de batalha (sem mostrar navios)
        painelCentral.remove(painelCentral.getComponent(3)); // remove antiga tela de batalha
        JPanel novaBatalha = criarTelaBatalha();
        painelCentral.add(novaBatalha, TELA_BATALHA);

        mostrarTelaBatalha();
    }

    // =========================================================================
    // MOSTRAR TELA DE BATALHA — aplica as restrições de turno nos botões
    // =========================================================================
    static void mostrarTelaBatalha() {

        cardLayout.show(painelCentral, TELA_BATALHA);

        if (turno == 1) {
            // J1 ataca: habilita tabuleiro do J2, bloqueia tabuleiro do J1
            for (int l = 0; l < 10; l++) for (int c = 0; c < 10; c++) {
                botoesJ1[l][c].setEnabled(false); // J1 não ataca a si mesmo
                botoesJ2[l][c].setEnabled(!atacadoJ2[l][c]); // J1 ataca aqui
            }
            // Oculta navios do J2 (J1 não pode ver)
            for (int l = 0; l < 10; l++) for (int c = 0; c < 10; c++) {
                if (naviosJ2[l][c] && !atacadoJ2[l][c]) {
                    botoesJ2[l][c].setText("🌊");
                    botoesJ2[l][c].setBackground(new Color(70, 130, 180));
                }
            }
            lblTurno.setText("🔵 Vez do Jogador 1 atacar o tabuleiro vermelho →");

        } else {
            // J2 ataca: habilita tabuleiro do J1, bloqueia tabuleiro do J2
            for (int l = 0; l < 10; l++) for (int c = 0; c < 10; c++) {
                botoesJ2[l][c].setEnabled(false); // J2 não ataca a si mesmo
                botoesJ1[l][c].setEnabled(!atacadoJ1[l][c]); // J2 ataca aqui
            }
            // Oculta navios do J1 (J2 não pode ver)
            for (int l = 0; l < 10; l++) for (int c = 0; c < 10; c++) {
                if (naviosJ1[l][c] && !atacadoJ1[l][c]) {
                    botoesJ1[l][c].setText("🌊");
                    botoesJ1[l][c].setBackground(new Color(70, 130, 180));
                }
            }
            lblTurno.setText("🔴 Vez do Jogador 2 atacar o tabuleiro azul ←");
        }
    }

    // =========================================================================
    // ATIRAR — um jogador atira no tabuleiro do outro
    // =========================================================================
    static void atirar(int linha, int coluna, int quemAtira) {

        // Só deixa atirar se for a vez certa
        if (turno != quemAtira) return;

        boolean[][]  naviosAlvo;
        boolean[][]  marcacaoAtaque;
        JButton[][]  botoesAlvo;
        int[]        pontos;   // referência não funciona direto em Java, usamos array

        if (quemAtira == 1) {
            // J1 atira no tabuleiro de J2
            naviosAlvo      = naviosJ2;
            marcacaoAtaque  = atacadoJ2;
            botoesAlvo      = botoesJ2;
        } else {
            // J2 atira no tabuleiro de J1
            naviosAlvo      = naviosJ1;
            marcacaoAtaque  = atacadoJ1;
            botoesAlvo      = botoesJ1;
        }

        if (marcacaoAtaque[linha][coluna]) return; // já atacou aqui
        marcacaoAtaque[linha][coluna] = true;

        String proximoJogador = (quemAtira == 1) ? "Jogador 2" : "Jogador 1";
        String mensagemOK;

        if (naviosAlvo[linha][coluna]) {
            // ACERTO 💥
            botoesAlvo[linha][coluna].setText("💥");
            botoesAlvo[linha][coluna].setBackground(new Color(220, 20, 60));
            botoesAlvo[linha][coluna].setEnabled(false);

            if (quemAtira == 1) {
                pontosJ1 += 100;
                acertosJ1++;
                tirosJ1--;
                lblPontosJ1.setText("⭐ J1: " + pontosJ1 + " pts");
                lblTirosJ1 .setText("🎯 Tiros: " + tirosJ1);
                lblStatus  .setText("💥 J1 acertou! +" + 100 + " pts");
            } else {
                pontosJ2 += 100;
                acertosJ2++;
                tirosJ2--;
                lblPontosJ2.setText("⭐ J2: " + pontosJ2 + " pts");
                lblTirosJ2 .setText("🎯 Tiros: " + tirosJ2);
                lblStatus  .setText("💥 J2 acertou! +" + 100 + " pts");
            }

            // Verifica vitória antes de pedir OK
            if (quemAtira == 1 && acertosJ1 >= TOTAL_NAVIOS) { fimDeJogo(1); return; }
            if (quemAtira == 2 && acertosJ2 >= TOTAL_NAVIOS) { fimDeJogo(2); return; }

            mensagemOK = "💥 ACERTO! Jogador " + quemAtira + " acertou um navio!\n"
                    + "➕ +" + 100 + " pontos\n\n"
                    + "Passe o computador para o " + proximoJogador + " e clique OK.";

        } else {
            // ÁGUA 💦
            botoesAlvo[linha][coluna].setText("💦");
            botoesAlvo[linha][coluna].setBackground(new Color(0, 102, 167));
            botoesAlvo[linha][coluna].setEnabled(false);

            if (quemAtira == 1) {
                tirosJ1--;
                lblTirosJ1.setText("🎯 Tiros: " + tirosJ1);
                lblStatus .setText("💧 J1 errou! " + tirosJ1 + " tiros restantes.");
            } else {
                tirosJ2--;
                lblTirosJ2.setText("🎯 Tiros: " + tirosJ2);
                lblStatus .setText("💧 J2 errou! " + tirosJ2 + " tiros restantes.");
            }

            // Verifica se acabaram os tiros
            if (quemAtira == 1 && tirosJ1 <= 0) { fimDeJogo(0); return; }
            if (quemAtira == 2 && tirosJ2 <= 0) { fimDeJogo(0); return; }

            mensagemOK = "💧 ÁGUA! Jogador " + quemAtira + " errou o tiro.\n"
                    + "Tiros restantes: " + (quemAtira == 1 ? tirosJ1 : tirosJ2) + "\n\n"
                    + "Passe o computador para o " + proximoJogador + " e clique OK.";
        }

        // Mostra resultado e pede OK antes de passar a vez
        JOptionPane.showMessageDialog(
                janela,
                mensagemOK,
                "Resultado do tiro — Jogador " + quemAtira,
                naviosAlvo[linha][coluna]
                        ? JOptionPane.INFORMATION_MESSAGE
                        : JOptionPane.WARNING_MESSAGE
        );

        // Troca o turno e mostra tela de bloqueio
        turno = (quemAtira == 1) ? 2 : 1;
        lblAviso.setText("Vez do " + proximoJogador + ". Clique em 'Estou pronto' para jogar.");
        cardLayout.show(painelCentral, TELA_AVISO);
    }

    // =========================================================================
    // FIM DE JOGO
    // =========================================================================
    static void fimDeJogo(int vencedor) {

        // Desabilita tudo
        for (int l = 0; l < 10; l++) for (int c = 0; c < 10; c++) {
            botoesJ1[l][c].setEnabled(false);
            botoesJ2[l][c].setEnabled(false);
        }

        String msg;
        if (vencedor == 1)      msg = "🏆 Jogador 1 venceu!\nAfundou toda a frota do J2!\n\n⭐ J1: " + pontosJ1 + " pts\n⭐ J2: " + pontosJ2 + " pts";
        else if (vencedor == 2) msg = "🏆 Jogador 2 venceu!\nAfundou toda a frota do J1!\n\n⭐ J1: " + pontosJ1 + " pts\n⭐ J2: " + pontosJ2 + " pts";
        else                    msg = "💀 Tiros esgotados!\nEmpate ou ninguém ganhou.\n\n⭐ J1: " + pontosJ1 + " pts\n⭐ J2: " + pontosJ2 + " pts";

        lblTurno.setText(vencedor > 0 ? "🏆 Jogador " + vencedor + " venceu!" : "💀 Fim de jogo!");
        cardLayout.show(painelCentral, TELA_BATALHA);

        int r = JOptionPane.showConfirmDialog(janela, msg + "\n\nJogar novamente?", "Fim de Jogo", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) resetar();
        else System.exit(0);
    }

    // =========================================================================
    // AUTO-POSICIONAR — posiciona todos os navios aleatoriamente
    // =========================================================================
    static void autoPosicionar() {
        if (faseSetup >= 2) return;

        int jogador = faseSetup == 0 ? 1 : 2;
        boolean[][] tabuleiro = jogador == 1 ? naviosJ1 : naviosJ2;
        JButton[][] grade     = jogador == 1 ? botoesJ1 : botoesJ2;

        // Limpa
        for (int l = 0; l < 10; l++) for (int c = 0; c < 10; c++) {
            tabuleiro[l][c] = false;
            grade[l][c].setText("🌊");
            grade[l][c].setBackground(new Color(70, 130, 180));
        }

        posicionarAleatorio(tabuleiro);

        for (int l = 0; l < 10; l++) for (int c = 0; c < 10; c++)
            if (tabuleiro[l][c]) {
                grade[l][c].setText("🚢");
                grade[l][c].setBackground(new Color(100, 100, 115));
            }

        // Após auto-posicionar, avança a fase igual ao clique manual
        navioAtual = TAMANHOS.length;
        avancarFaseSetup(jogador);
    }

    // =========================================================================
    // AVANÇAR FASE SETUP — chamado quando todos os navios foram posicionados
    // =========================================================================
    static void avancarFaseSetup(int jogador) {
        navioAtual = 0;
        horizontal = true;
        btnDirecao.setText("↔ Horizontal");

        if (jogador == 1) {
            faseSetup = 1;
            lblAviso.setText("Jogador 1 terminou! Vez do Jogador 2 posicionar.");
            lblTurno.setText("🎮 Passando para o Jogador 2...");
            cardLayout.show(painelCentral, TELA_AVISO);
        } else {
            faseSetup = 2;
            lblAviso.setText("Jogador 2 terminou! Vamos começar a batalha.");
            lblTurno.setText("🎮 Tudo pronto! Começando batalha...");
            cardLayout.show(painelCentral, TELA_AVISO);
        }
    }

    // =========================================================================
    // COLOCAR NAVIO — jogador clica no próprio tabuleiro durante o setup
    // =========================================================================
    static void colocarNavio(int linha, int coluna, int jogador) {

        // Ignora clique se não for a vez deste jogador
        if (faseSetup == 0 && jogador != 1) return;
        if (faseSetup == 1 && jogador != 2) return;
        if (faseSetup >= 2) return;

        if (navioAtual >= TAMANHOS.length) return;

        int tamanho = TAMANHOS[navioAtual];
        boolean[][] tabuleiro = jogador == 1 ? naviosJ1 : naviosJ2;
        JButton[][] grade     = jogador == 1 ? botoesJ1 : botoesJ2;

        if (!cabeFora(tabuleiro, linha, coluna, tamanho, horizontal)) {
            lblStatus.setText("⚠ Posição inválida! Tente outro lugar.");
            return;
        }

        for (int i = 0; i < tamanho; i++) {
            int l = horizontal ? linha       : linha + i;
            int c = horizontal ? coluna + i  : coluna;
            tabuleiro[l][c] = true;
            grade[l][c].setText("🚢");
            grade[l][c].setBackground(new Color(100, 100, 115));
        }

        navioAtual++;
        lblStatus.setText(navioAtual < TAMANHOS.length
                ? "✔ Posicionado! Agora: " + NOMES[navioAtual]
                : "✔ Todos posicionados!");

        // Se todos os navios foram colocados, avança para a próxima fase
        if (navioAtual >= TAMANHOS.length) {
            avancarFaseSetup(jogador);
        }
    }

    // =========================================================================
    // POSICIONAR ALEATÓRIO
    // =========================================================================
    static void posicionarAleatorio(boolean[][] tabuleiro) {
        Random random = new Random();
        for (int tamanho : TAMANHOS) {
            boolean colocou = false;
            while (!colocou) {
                boolean horiz = random.nextBoolean();
                int l = random.nextInt(10);
                int c = random.nextInt(10);
                if (cabeFora(tabuleiro, l, c, tamanho, horiz)) {
                    for (int i = 0; i < tamanho; i++) {
                        int ll = horiz ? l     : l + i;
                        int cc = horiz ? c + i : c;
                        tabuleiro[ll][cc] = true;
                    }
                    colocou = true;
                }
            }
        }
    }

    // =========================================================================
    // CABE FORA — verifica se o navio cabe sem sair nem colidir
    // =========================================================================
    static boolean cabeFora(boolean[][] tabuleiro, int linha, int coluna, int tamanho, boolean horiz) {
        for (int i = 0; i < tamanho; i++) {
            int l = horiz ? linha       : linha + i;
            int c = horiz ? coluna + i  : coluna;
            if (l >= 10 || c >= 10) return false;
            if (tabuleiro[l][c])    return false;
        }
        return true;
    }

    // =========================================================================
    // RESETAR — reinicia tudo do zero
    // =========================================================================
    static void resetar() {
        pontosJ1 = 0; pontosJ2 = 0;
        tirosJ1  = 30; tirosJ2 = 30;
        acertosJ1 = 0; acertosJ2 = 0;
        navioAtual = 0; horizontal = true;
        faseSetup = 0; turno = 1;
        naviosJ1   = new boolean[10][10];
        naviosJ2   = new boolean[10][10];
        atacadoJ1  = new boolean[10][10];
        atacadoJ2  = new boolean[10][10];
        botoesJ1   = new JButton[10][10];
        botoesJ2   = new JButton[10][10];
        janela.dispose();
        main(null);
    }
}