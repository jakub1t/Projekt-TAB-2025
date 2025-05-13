package com.polsl.firmakurierska.dto;


import org.springframework.hateoas.RepresentationModel;

import com.polsl.firmakurierska.controller.PracownikController;
import com.polsl.firmakurierska.controller.PrawoJazdyController;
import com.polsl.firmakurierska.model.Pracownik;
import com.polsl.firmakurierska.model.PrawoJazdy;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class PrawoJazdyDTO extends RepresentationModel<PrawoJazdyDTO>{
	
	private Integer idKat;
	private String kategoria;
	
	public PrawoJazdyDTO(PrawoJazdy prawoJazdy) {
		this.idKat = prawoJazdy.getIdKat();
		this.kategoria = prawoJazdy.getKategoria();
		
		this.add(linkTo(methodOn(PrawoJazdyController.class)
				.getPrawoJazdyById(prawoJazdy.getIdKat().toString()))
				.withSelfRel());
		
		if (prawoJazdy.getPracownicy() != null) {
		    int i = 0;
		    for (Pracownik p : prawoJazdy.getPracownicy()) {
		        this.add(linkTo(methodOn(PracownikController.class)
		                .getPracownikById(p.getIdOsoby().toString()))
		                .withRel("pracownik_" + (++i)));
		    }
		}
	}
}
