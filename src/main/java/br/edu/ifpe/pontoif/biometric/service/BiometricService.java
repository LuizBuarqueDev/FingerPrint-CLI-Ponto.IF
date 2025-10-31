package br.edu.ifpe.pontoif.biometric.service;

import br.edu.ifpe.pontoif.biometric.capture.FutronicSdkCapture;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
public class BiometricService {

    private final FutronicSdkCapture capture;

    public BiometricService() {
        this.capture = new FutronicSdkCapture();
    }

    public String captureImageBase64() {
        byte[] bytes = capture.captureImageBytes();
        if (bytes == null || bytes.length == 0) return null;
        return Base64.getEncoder().encodeToString(bytes);
    }
}
