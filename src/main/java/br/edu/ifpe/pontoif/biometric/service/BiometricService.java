package br.edu.ifpe.pontoif.biometric.service;

import br.edu.ifpe.pontoif.biometric.capture.FutronicSdkCapture;
import br.edu.ifpe.pontoif.biometric.dto.BiometricMatchResultDTO;
import br.edu.ifpe.pontoif.biometric.dto.BiometricSampleDTO;
import br.edu.ifpe.pontoif.biometric.dto.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BiometricService {

    private final FutronicSdkCapture capture;
    private final RestTemplate rest;

    private final String remoteRegisterUrl =
            "http://132.226.159.21:8081/biometric/enroll";

    private final String remoteSampleUrl =
            "http://132.226.159.21:8081/api/biometric/match";

    public BiometricService() {
        this.capture = new FutronicSdkCapture();
        this.rest = new RestTemplate();
    }

    // =========================================================
    // REGISTER FLOW
    // =========================================================
    public boolean captureAndRegister(UUID userId) {

        System.out.println("👉 Starting biometric capture for user: " + userId);

        byte[] bytes = capture.captureImageBytes();
        if (bytes == null || bytes.length == 0) {
            System.err.println("❌ Capture failed or empty image.");
            return false;
        }

        String base64 = Base64.getEncoder().encodeToString(bytes);
        String json = String.format(
                "{\"user\":\"%s\",\"image\":\"%s\"}",
                userId,
                escapeJson(base64)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> resp =
                    rest.postForEntity(remoteRegisterUrl, entity, String.class);

            System.out.println("📤 Remote API response: " + resp.getStatusCode());
            return resp.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            System.err.println("⚠️ Error sending to remote API: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // MATCH FLOW (RETORNA OBJETO PARA O FRONT)
    // =========================================================
    public Optional<BiometricMatchResultDTO> captureAndSendSample(Long sessionId) {

        System.out.println("👉 Starting verification capture for session: " + sessionId);

        byte[] bytes = capture.captureImageBytes();
        if (bytes == null || bytes.length == 0) {
            System.err.println("❌ Capture failed or empty image.");
            return Optional.empty();
        }

        String base64 = Base64.getEncoder().encodeToString(bytes);

        BiometricSampleDTO dto = new BiometricSampleDTO(
                Role.STUDENT,
                sessionId,
                base64
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BiometricSampleDTO> entity =
                new HttpEntity<>(dto, headers);

        try {
            ResponseEntity<BiometricMatchResultDTO> resp =
                    rest.postForEntity(
                            remoteSampleUrl,
                            entity,
                            BiometricMatchResultDTO.class
                    );

            System.out.println("📤 Remote API response: " + resp.getStatusCode());

            return Optional.ofNullable(resp.getBody());

        } catch (Exception e) {
            System.err.println("⚠️ Error sending to remote API: " + e.getMessage());
            return Optional.empty();
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}