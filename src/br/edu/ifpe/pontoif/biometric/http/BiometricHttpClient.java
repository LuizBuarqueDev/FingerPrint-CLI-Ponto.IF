package br.edu.ifpe.pontoif.biometric.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

public class BiometricHttpClient {

    private final HttpClient client;
    private final String endpointUrl;

    public BiometricHttpClient(String endpointUrl) {
        this.endpointUrl = endpointUrl;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public Optional<String> postJson(String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointUrl))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            int status = resp.statusCode();
            String body = resp.body();

            if (status >= 200 && status < 300) {
                return Optional.ofNullable(body);
            } else {
                System.err.printf("❌ HTTP %d: %s%n", status, body);
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro HTTP: " + e.getMessage());
            return Optional.empty();
        }
    }
}