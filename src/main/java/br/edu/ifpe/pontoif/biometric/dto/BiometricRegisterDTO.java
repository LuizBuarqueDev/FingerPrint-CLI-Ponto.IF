package br.edu.ifpe.pontoif.biometric.dto;

import java.util.UUID;

public record BiometricRegisterDTO(UUID userId, String imageBase64) {}
