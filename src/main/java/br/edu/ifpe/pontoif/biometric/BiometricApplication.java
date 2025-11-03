package br.edu.ifpe.pontoif.biometric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BiometricApplication {
    public static void main(String[] args) {
        System.loadLibrary("ftrJSDK64");
        SpringApplication.run(BiometricApplication.class, args);
    }
}