package com.polsl.firmakurierska.dto;


import org.springframework.hateoas.RepresentationModel;

import com.polsl.firmakurierska.controller.DostawaController;
import com.polsl.firmakurierska.controller.KlientController;
import com.polsl.firmakurierska.controller.PaczkaController;
import com.polsl.firmakurierska.model.Paczka;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class PaczkaDTO extends RepresentationModel<DostawaDTO> {

    private Integer idPaczki;
	private double wagaPaczki;
    
    public PaczkaDTO(Paczka paczka) {
        this.idPaczki = paczka.getIdPaczki();
        this.wagaPaczki = paczka.getWagaPaczki();

   
	this.add(linkTo(methodOn(PaczkaController.class).getPaczkaById(paczka.getIdPaczki().toString()))
                .withSelfRel());

	    if (paczka.getDostawa() != null) {
	       this.add(linkTo(methodOn(DostawaController.class)
	               .getDostawaById(paczka.getDostawa().getIdDostawy().toString()))
	               .withRel("dostawa"));
	    }
	    
        if (paczka.getKlient() != null) {
            this.add(linkTo(methodOn(KlientController.class)
                    .getKlientById(paczka.getKlient().getIdKlienta()))
                    .withRel("klient"));
        }
        
    }    
    
    
}
