package br.edu.ifpe.pontoif.biometric.service;

import br.edu.ifpe.pontoif.biometric.capture.FutronicSdkCapture;
import br.edu.ifpe.pontoif.biometric.http.BiometricHttpClient;

import java.util.Base64;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class BiometricServiceCLI {

    private final FutronicSdkCapture capture;
    private final BiometricHttpClient registerClient;
    private final BiometricHttpClient sampleClient;

    public BiometricServiceCLI(FutronicSdkCapture capture,
                               BiometricHttpClient registerClient,
                               BiometricHttpClient sampleClient) {
        this.capture = capture;
        this.registerClient = registerClient;
        this.sampleClient = sampleClient;
    }

    // === 1️⃣ Cadastro ===
    public void registerBiometric() {
        Scanner sc = new Scanner(System.in);
        System.out.print("UUID do usuário para cadastro: ");
        String input = sc.nextLine().trim();

        UUID userId;
        try {
            userId = UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            System.err.println("UUID inválido!");
            return;
        }

        System.out.println("👉 Coloque o dedo no sensor...");
        byte[] imageBytes = capture.captureImageBytes(); // agora captura imagem PNG
        if (imageBytes == null || imageBytes.length == 0) {
            System.err.println("❌ Falha na captura da imagem.");
            return;
        }

        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        String json = "{"
                + "\"user\": \"" + userId + "\", "
                + "\"image\": \"" + escapeJson(base64) + "\""
                + "}";


        System.out.println("📤 Enviando imagem biométrica ao backend...");
        Optional<String> resp = registerClient.postJson(json);

        resp.ifPresentOrElse(
                body -> System.out.println("✅ Biometria cadastrada com sucesso."),
                () -> System.err.println("⚠️ Falha ao cadastrar biometria.")
        );
    }


    public void sendSample() {
        System.out.println("👉 Coloque o dedo no sensor para verificação...");
        byte[] imageBytes = capture.captureImageBytes(); // captura imagem PNG

        if (imageBytes == null || imageBytes.length == 0) {
            System.err.println("❌ Falha na captura da imagem.");
            return;
        }

        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        String json = "{ \"image\": \"" + escapeJson(base64) + "\" }";

        System.out.println("📤 Enviando imagem para verificação...");
        Optional<String> resp = sampleClient.postJson(json);

        resp.ifPresentOrElse(
                body -> System.out.println("✅ Biometria compatível (match encontrado)."),
                () -> System.err.println("❌ Nenhum match encontrado (HTTP 404).")
        );
    }


    public void captureAndShow() {
        System.out.println("👉 Coloque o dedo no sensor...");
        byte[] imageBytes = capture.captureImageBytes();

        if (imageBytes == null || imageBytes.length == 0) {
            System.err.println("❌ Falha na captura da imagem.");
            return;
        }

        String b64 = Base64.getEncoder().encodeToString(imageBytes);
        System.out.println("📋 Imagem capturada em Base64:");
        System.out.println(b64);
        System.out.println("Tamanho: " + imageBytes.length + " bytes");
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
