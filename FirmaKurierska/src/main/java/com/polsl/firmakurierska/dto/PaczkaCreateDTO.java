package com.polsl.firmakurierska.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class PaczkaCreateDTO {

    @NotNull(message = "Waga paczki jest wymagana")
    @Positive(message = "Waga paczki musi być większa od 0")
    private Double wagaPaczki;

    private Integer klientId;

    private Integer dostawaId;

    // Lista ID produktów które mają być przypisane do paczki
    private List<Integer> produktIds;

    // Gettery i settery
    public Double getWagaPaczki() {
        return wagaPaczki;
    }

    public void setWagaPaczki(Double wagaPaczki) {
        this.wagaPaczki = wagaPaczki;
    }

    public Integer getKlientId() {
        return klientId;
    }

    public void setKlientId(Integer klientId) {
        this.klientId = klientId;
    }

    public Integer getDostawaId() {
        return dostawaId;
    }

    public void setDostawaId(Integer dostawaId) {
        this.dostawaId = dostawaId;
    }

    public List<Integer> getProduktIds() {
        return produktIds;
    }

    public void setProduktIds(List<Integer> produktIds) {
        this.produktIds = produktIds;
    }
}
