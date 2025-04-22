package com.polsl.firmakurierska.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

 
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "producent")
public class Producent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idProducenta;
	
	private String nazwaProducenta;
	
	@OneToMany(mappedBy = "producent", cascade = CascadeType.ALL)
    @JsonIgnore
	private List<Produkt> produkt;
}
