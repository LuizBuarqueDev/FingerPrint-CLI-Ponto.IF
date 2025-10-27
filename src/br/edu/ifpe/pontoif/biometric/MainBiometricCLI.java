package br.edu.ifpe.pontoif.biometric;

import br.edu.ifpe.pontoif.biometric.capture.FutronicSdkCapture;
import br.edu.ifpe.pontoif.biometric.http.BiometricHttpClient;
import br.edu.ifpe.pontoif.biometric.service.BiometricServiceCLI;
import br.edu.ifpe.pontoif.biometric.cli.BiometricCLI;

public class MainBiometricCLI {

    public static void main(String[] args) {
        System.loadLibrary("ftrJSDK64");

        String registerUrl = "http://localhost:8080/biometric";
        String sampleUrl = "http://localhost:8080/biometric/sample";

        BiometricServiceCLI service = new BiometricServiceCLI(
                new FutronicSdkCapture(),
                new BiometricHttpClient(registerUrl),
                new BiometricHttpClient(sampleUrl)
        );

        BiometricCLI cli = new BiometricCLI(service);
        cli.start();
    }
}