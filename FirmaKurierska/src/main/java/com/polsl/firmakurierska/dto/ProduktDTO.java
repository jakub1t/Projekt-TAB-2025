package com.polsl.firmakurierska.dto;


import org.springframework.hateoas.RepresentationModel;

import com.polsl.firmakurierska.controller.PaczkaController;
import com.polsl.firmakurierska.controller.ProducentController;
import com.polsl.firmakurierska.controller.ProduktController;
import com.polsl.firmakurierska.model.Produkt;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class ProduktDTO extends RepresentationModel<ProduktDTO> {

	private Integer idProduktu;
	private String nrSeryjny;
	private String kategoriaProd;
	private String nazwaProduktu;
	private double waga;
	
    public ProduktDTO() {}

    public ProduktDTO(Produkt produkt) {
        this.idProduktu = produkt.getIdProduktu();
        this.nrSeryjny = produkt.getNrSeryjny();
        this.kategoriaProd = produkt.getKategoriaProd();
        this.nazwaProduktu = produkt.getNazwaProduktu();
        this.waga = produkt.getWaga();

   
	this.add(linkTo(methodOn(ProduktController.class).getProduktById(produkt.getIdProduktu().toString()))
                .withSelfRel());

	    if (produkt.getPaczka() != null) {
	       this.add(linkTo(methodOn(PaczkaController.class)
	               .getPaczkaById(produkt.getPaczka().getIdPaczki().toString()))
	               .withRel("paczka"));
	    }
	    
        if (produkt.getProducent() != null) {
            this.add(linkTo(methodOn(ProducentController.class)
                    .getProducentById(produkt.getProducent().getIdProducenta().toString()))
                    .withRel("producent"));
        }
        
    }    
    
    
}
