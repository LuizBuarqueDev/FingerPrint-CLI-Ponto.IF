package br.edu.ifpe.pontoif.biometric.controller;

import br.edu.ifpe.pontoif.biometric.dto.BiometricRegisterRequest;
import br.edu.ifpe.pontoif.biometric.dto.BiometricSampleRequest;
import br.edu.ifpe.pontoif.biometric.service.BiometricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/biometric")
@RequiredArgsConstructor
@Tag(name = "Biometric Capture", description = "Handles local fingerprint capture and communication with remote Ponto.IF API.")
public class BiometricController {

    private final BiometricService service;

    @Operation(summary = "Capture fingerprint and send for registration")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody BiometricRegisterRequest req) {
        System.out.println(req.toString());
        boolean ok = service.captureAndRegister(req.userId());
        return ok
                ? ResponseEntity.ok("✅ Biometric registered successfully.")
                : ResponseEntity.internalServerError().body("❌ Failed to register biometric.");
    }

    @Operation(summary = "Capture fingerprint and send for verification")
    @PostMapping("/match")
    public ResponseEntity<String> sendSample(@RequestBody BiometricSampleRequest req) {
        boolean ok = service.captureAndSendSample(req.sessionId());
        return ok
                ? ResponseEntity.ok("✅ Biometric sample sent successfully.")
                : ResponseEntity.internalServerError().body("❌ Failed to send sample.");
    }
}