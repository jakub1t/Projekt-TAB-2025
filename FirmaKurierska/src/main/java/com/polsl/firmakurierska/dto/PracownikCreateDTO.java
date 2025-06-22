package com.polsl.firmakurierska.dto;

import lombok.Data;

import java.util.Set;

@Data
public class PracownikCreateDTO {
    private String imie;
    private String nazwisko;
    private String pesel;
    private KontoCreateDTO konto;
    private Integer stanowiskoId;
    private Set<Integer> prawaJazdyIds; // opcjonalnie
}
