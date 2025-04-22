package com.polsl.firmakurierska.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

 
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "paczka")
public class Paczka {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idPaczki;
	private double wagaPaczki;
	
	@ManyToOne(cascade = CascadeType.ALL)
	   @JoinColumn(name = "fk_klient_id")  
	   private Klient klient;
	
	@OneToMany(mappedBy = "paczka", cascade = CascadeType.ALL)
    @JsonIgnore
	private List<Produkt> produkt;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_dostawa_id")
	@JsonBackReference
	//zapobiegają błędowi nieskończonej rekursji w JSON
	private Dostawa dostawa;
}