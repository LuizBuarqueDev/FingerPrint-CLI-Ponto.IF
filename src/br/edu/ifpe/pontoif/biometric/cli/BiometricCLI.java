package br.edu.ifpe.pontoif.biometric.cli;

import br.edu.ifpe.pontoif.biometric.service.BiometricServiceCLI;
import java.util.Scanner;

public class BiometricCLI {

    private final BiometricServiceCLI service;
    private final Scanner scanner = new Scanner(System.in);

    public BiometricCLI(BiometricServiceCLI service) {
        this.service = service;
    }

    public void start() {
        System.out.println("=== CLI Biométrico Ponto.IF ===");

        while (true) {
            System.out.println("\nEscolha uma opção:");
            System.out.println("1 - Cadastrar biometria (envia user + template)");
            System.out.println("2 - Enviar sample para verificação (somente template)");
            System.out.println("3 - Capturar e mostrar Base64 (debug)");
            System.out.println("0 - Sair");
            System.out.print("> ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> service.registerBiometric();
                case "2" -> service.sendSample();
                case "3" -> service.captureAndShow();
                case "0" -> {
                    System.out.println("Encerrando...");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }
}
