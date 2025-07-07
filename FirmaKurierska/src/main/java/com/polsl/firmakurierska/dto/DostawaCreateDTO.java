package com.polsl.firmakurierska.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DostawaCreateDTO {
    
    private LocalDate dataWyruszenia;
    private LocalDate termin;
    private String punktA;
    private String punktB;
    private String status;
    private Integer idPojazdu;
    private Integer driverId;
    private List<Integer> paczki = new ArrayList<>();
    private List<Integer> usedPaczki = new ArrayList<>();

    public LocalDate getDataWyruszenia() {
        return dataWyruszenia;
    }
    public void setDataWyruszenia(LocalDate dataWyruszenia) {
        this.dataWyruszenia = dataWyruszenia;
    }
    public LocalDate getTermin() {
        return termin;
    }
    public void setTermin(LocalDate termin) {
        this.termin = termin;
    }
    public String getPunktA() {
        return punktA;
    }
    public void setPunktA(String punktA) {
        this.punktA = punktA;
    }
    public String getPunktB() {
        return punktB;
    }
    public void setPunktB(String punktB) {
        this.punktB = punktB;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Integer getIdPojazdu() {
        return idPojazdu;
    }
    public void setIdPojazdu(Integer idPojazdu) {
        this.idPojazdu = idPojazdu;
    }
    public Integer getDriverId() {
        return driverId;
    }
    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }
    public List<Integer> getPaczki() {
        return paczki;
    }
    public void setPaczki(List<Integer> paczki) {
        this.paczki = paczki;
    }
    public List<Integer> getUsedPaczki() {
        return usedPaczki;
    }
    public void setUsedPaczki(List<Integer> usedPaczki) {
        this.usedPaczki = usedPaczki;
    }
}
