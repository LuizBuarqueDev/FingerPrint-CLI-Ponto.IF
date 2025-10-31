package br.edu.ifpe.pontoif.biometric.service;

import br.edu.ifpe.pontoif.biometric.capture.FutronicSdkCapture;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BiometricService {

    private final FutronicSdkCapture capture;
    private final RestTemplate rest;

    private final String remoteRegisterUrl = "http://localhost:8080/biometric";
    private final String remoteSampleUrl = "http://localhost:8080/biometric/sample";

    public BiometricService() {
        this.capture = new FutronicSdkCapture();
        this.rest = new RestTemplate();
    }

    // === REGISTER FLOW ===
    public boolean captureAndRegister(UUID userId) {
        System.out.println("👉 Starting biometric capture for user: " + userId);

        byte[] bytes = capture.captureImageBytes();
        if (bytes == null || bytes.length == 0) {
            System.err.println("❌ Capture failed or empty image.");
            return false;
        }

        String base64 = Base64.getEncoder().encodeToString(bytes);
        String json = String.format("{\"user\":\"%s\",\"image\":\"%s\"}", userId, escapeJson(base64));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> resp = rest.postForEntity(remoteRegisterUrl, entity, String.class);
            System.out.println("📤 Remote API response: " + resp.getStatusCode());
            return resp.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("⚠️ Error sending to remote API: " + e.getMessage());
            return false;
        }
    }

    // === SAMPLE FLOW ===
    public boolean captureAndSendSample(String enrollee) {
        System.out.println("👉 Starting verification capture for enrollee: " + enrollee);

        byte[] bytes = capture.captureImageBytes();
        if (bytes == null || bytes.length == 0) {
            System.err.println("❌ Capture failed or empty image.");
            return false;
        }

        String base64 = Base64.getEncoder().encodeToString(bytes);
        String json = String.format("{\"enrollee\":\"%s\",\"image\":\"%s\"}", enrollee, escapeJson(base64));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> resp = rest.postForEntity(remoteSampleUrl, entity, String.class);
            System.out.println("📤 Remote API response: " + resp.getStatusCode());
            return resp.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("⚠️ Error sending to remote API: " + e.getMessage());
            return false;
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}