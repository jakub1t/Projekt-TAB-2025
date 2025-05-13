package com.polsl.firmakurierska.dto;


import org.springframework.hateoas.RepresentationModel;

import com.polsl.firmakurierska.controller.StanowiskoController;
import com.polsl.firmakurierska.controller.KontoController;
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
public class PracownikDTO extends RepresentationModel<PracownikDTO>{

	private Integer idOsoby; 
	private String imie; 
	private String nazwisko;  
	private String pesel; 
	
	public PracownikDTO(Pracownik pracownik) {
		this.idOsoby = pracownik.getIdOsoby();
		this.imie = pracownik.getImie();
		this.nazwisko = pracownik.getNazwisko();
		this.pesel = pracownik.getPesel();
		
        this.add(linkTo(methodOn(PracownikController.class).getPracownikById(pracownik.getIdOsoby().toString()))
        		.withSelfRel());
        
        if(pracownik.getStanowisko() != null) {
        	this.add(linkTo(methodOn(StanowiskoController.class)
        			.getStanowiskoById(pracownik.getStanowisko().getIdStanowisko()))
        			.withRel("stanowisko"));
        }
        
        if(pracownik.getKonto() != null) {
        	this.add(linkTo(methodOn(KontoController.class)
        			.getKontoById(pracownik.getKonto().getIdKonta()))
        			.withRel("konto"));
        }
        
        if(pracownik.getPrawoJazdy() != null) {
        	for (PrawoJazdy pj : pracownik.getPrawoJazdy()) {
        		this.add(linkTo(methodOn(PrawoJazdyController.class)
            			.getPrawoJazdyById(pj.getIdKat().toString()))
            			.withRel("prawo_jazdy"));
        	}
        }
	}
	
}
