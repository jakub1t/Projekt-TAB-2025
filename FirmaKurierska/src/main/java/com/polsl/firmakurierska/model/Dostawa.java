package com.polsl.firmakurierska.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dostawa")
public class Dostawa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idDostawy;
	
	private LocalDate dataWyruszenia;
	private LocalDate termin;
	private String punktA;
	private String punktB;
	
	@OneToMany(mappedBy = "dostawa", fetch = FetchType.EAGER)
	@JsonManagedReference
	@JsonIgnore
	//zapobiegają błędowi nieskończonej rekursji w JSON
	private List<Paczka> paczki;
	
	@ManyToOne
	@JoinColumn(name = "pojazd_id")
	private Pojazd pojazd;

	@ManyToOne
	@JoinColumn(name = "pracownik_id")
	private Pracownik pracownik;

	private String status;
	

}