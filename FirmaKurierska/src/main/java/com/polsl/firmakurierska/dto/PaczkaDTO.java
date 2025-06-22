package com.polsl.firmakurierska.dto;


import org.springframework.hateoas.RepresentationModel;

import com.polsl.firmakurierska.controller.DostawaController;
import com.polsl.firmakurierska.controller.KlientController;
import com.polsl.firmakurierska.controller.PaczkaController;
import com.polsl.firmakurierska.model.Paczka;

import com.polsl.firmakurierska.model.Produkt;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class PaczkaDTO extends RepresentationModel<PaczkaDTO> {

    private Integer idPaczki;
	private Double wagaPaczki;
    private Integer klientId;
    private Integer dostawaId;
    private List<Integer> produktIds;
    
    public PaczkaDTO() {}

    public PaczkaDTO(Paczka paczka) {
        this.idPaczki = paczka.getIdPaczki();
        this.wagaPaczki = paczka.getWagaPaczki();

   
	if (paczka.getKlient() != null) {
            this.klientId = paczka.getKlient().getIdKlienta();
        }
        if (paczka.getDostawa() != null) {
            this.dostawaId = paczka.getDostawa().getIdDostawy();
        }
        if (paczka.getProdukt() != null) {
            this.produktIds = paczka.getProdukt().stream()
                                    .map(Produkt::getIdProduktu)
                                    .collect(Collectors.toList());
        }

        // Link do samej paczki (self)
        this.add(linkTo(methodOn(PaczkaController.class).getPaczkaById(paczka.getIdPaczki().toString()))
                .withSelfRel());

	    // Link do dostawy
        if (paczka.getDostawa() != null) {
            this.add(linkTo(methodOn(DostawaController.class)
                    .getDostawaById(paczka.getDostawa().getIdDostawy().toString()))
                    .withRel("dostawa"));
        }

        // Link do klienta
        if (paczka.getKlient() != null) {
            this.add(linkTo(methodOn(KlientController.class)
                    .getKlientById(paczka.getKlient().getIdKlienta()))
                    .withRel("klient"));
        }
    }    
}
