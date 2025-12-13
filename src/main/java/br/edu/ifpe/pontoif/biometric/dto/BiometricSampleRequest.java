package br.edu.ifpe.pontoif.biometric.dto;

import java.util.UUID;

public record BiometricSampleRequest (Role role, Long sessionId) {}
