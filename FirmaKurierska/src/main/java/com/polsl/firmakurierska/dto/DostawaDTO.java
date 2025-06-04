package com.polsl.firmakurierska.dto;

import com.polsl.firmakurierska.controller.DostawaController;
import com.polsl.firmakurierska.controller.PracownikController;
import com.polsl.firmakurierska.model.Dostawa;
import com.polsl.firmakurierska.model.Paczka;

import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class DostawaDTO extends RepresentationModel<DostawaDTO> {

    private Integer idDostawy;
    private LocalDate dataWyruszenia;
    private LocalDate termin;
    private String punktA;
    private String punktB;
    private String status;
    private Integer idPojazdu;
    private List<Integer> paczki = new ArrayList<>();

    public DostawaDTO() {}

    public DostawaDTO(Dostawa dostawa) {
        this.idDostawy = dostawa.getIdDostawy();
        this.dataWyruszenia = dostawa.getDataWyruszenia();
        this.termin = dostawa.getTermin();
        this.punktA = dostawa.getPunktA();
        this.punktB = dostawa.getPunktB();
        this.status = dostawa.getStatus();
        this.idPojazdu = null;

        this.add(linkTo(methodOn(DostawaController.class).getDostawaById(dostawa.getIdDostawy().toString()))
                .withSelfRel());

        if (dostawa.getPojazd() != null) {
            this.idPojazdu = dostawa.getPojazd().getIdPojazdu();
        }

        if (dostawa.getPaczki() != null) {
            for (Paczka p : dostawa.getPaczki()) {
                this.paczki.add(p.getIdPaczki());
            }
        }

        if (dostawa.getPracownik() != null) {
            this.add(linkTo(methodOn(PracownikController.class)
                    .getPracownikById(dostawa.getPracownik().getIdOsoby().toString()))
                    .withRel("pracownik"));
        }
    }
}
