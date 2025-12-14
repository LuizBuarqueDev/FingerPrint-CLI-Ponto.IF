package br.edu.ifpe.pontoif.biometric.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BiometricMatchResultDTO {

    private UUID biometricId;
    private UUID studentId;
    private Double score;
}

