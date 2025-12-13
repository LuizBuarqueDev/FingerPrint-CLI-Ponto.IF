package br.edu.ifpe.pontoif.biometric.dto;


public record BiometricSampleDTO(Role role, Long sessionId, String image) {}