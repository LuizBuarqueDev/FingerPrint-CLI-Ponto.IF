package br.edu.ifpe.pontoif.biometric.controller;

import br.edu.ifpe.pontoif.biometric.dto.BiometricRegisterDTO;
import br.edu.ifpe.pontoif.biometric.dto.BiometricSampleDTO;
import br.edu.ifpe.pontoif.biometric.service.BiometricService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/biometric")
public class BiometricController {

    private final BiometricService service;

    public BiometricController(BiometricService service) {
        this.service = service;
    }

    @PostMapping("/capture")
    public ResponseEntity<String> capture() {
        String base64 = service.captureImageBase64();
        if (base64 == null)
            return ResponseEntity.internalServerError().body("Erro na captura biométrica.");
        return ResponseEntity.ok(base64);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody BiometricRegisterDTO dto) {
        System.out.printf("📥 Cadastro recebido para usuário %s (%d chars de imagem)%n",
                dto.userId(), dto.imageBase64().length());
        // Aqui você faria o POST para o backend principal
        return ResponseEntity.ok("Biometria registrada!");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody BiometricSampleDTO dto) {
        System.out.printf("📥 Verificação recebida (%d chars de imagem)%n", dto.imageBase64().length());
        // Aqui você chamaria o serviço matcher (SourceAFIS, etc.)
        return ResponseEntity.ok("Verificação processada!");
    }
}