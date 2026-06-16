import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner leitor = new Scanner(System.in);

        double saldo = 1000.00;
        double valorSaque;
        double valorDeposito;
        int codigo = 0;

        int cemRatatouilles = 40;
        int cinquentaRatatouilles = 40;
        int vinteRatatouilles = 30;
        int cincoRatatouilles = 15;
        int doisRatatouilles = 15;

        // Carregar saldo salvo
        try {
            BufferedReader reader = new BufferedReader(new FileReader("saldo.txt"));
            saldo = Double.parseDouble(reader.readLine());
            reader.close();
            System.out.println("Saldo carregado com sucesso!");
        } catch (IOException e) {
            System.out.println("Primeira execução. Saldo inicial: 1000 ratatouilles.");
        }

        while (codigo != 4) {

            System.out.println("\n| ======= Bem Vindo Ao RatoBank! ======= |");
            System.out.println("| =======    Caixa Eletrônico    ======= |");
            System.out.println("|   1 - Consultar Saldo;                 |");
            System.out.println("|   2 - Realizar Saque;                  |");
            System.out.println("|   3 - Realizar Depósito;               |");
            System.out.println("|   4 - Sair                             |");
            System.out.println("==========================================");
            System.out.print("Informe a Opção Desejada: ");

            codigo = leitor.nextInt();

            switch (codigo) {

                case 1:
                    System.out.println("Seu saldo é de " + saldo + " ratatouilles.");
                    break;

                case 2:
                    System.out.print("Informe o valor do saque: ");
                    valorSaque = leitor.nextDouble();

                    if (valorSaque > saldo) {
                        System.out.println("Valor indisponível! Saldo disponível: " + saldo);
                    } else {
                        saldo -= valorSaque;
                        System.out.println("Saque de " + valorSaque + " ratatouilles efetuado.");
                    }
                    break;

                case 3:
                    System.out.print("Informe o valor do depósito: ");
                    valorDeposito = leitor.nextDouble();

                    while (valorDeposito <= 0) {
                        System.out.print("Valor inválido! Digite um valor positivo: ");
                        valorDeposito = leitor.nextDouble();
                    }

                    saldo += valorDeposito;
                    System.out.println("Depósito realizado com sucesso!");
                    break;

                case 4:

                    // Salvar saldo antes de sair
                    try {
                        FileWriter writer = new FileWriter("saldo.txt");
                        writer.write(String.valueOf(saldo));
                        writer.close();

                        System.out.println("Saldo salvo com sucesso!");
                    } catch (IOException e) {
                        System.out.println("Erro ao salvar saldo.");
                    }

                    System.out.println("Obrigado por utilizar o RatoBank!");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        }

        leitor.close();
    }
}