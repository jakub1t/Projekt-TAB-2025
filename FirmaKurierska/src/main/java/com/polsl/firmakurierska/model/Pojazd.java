package com.polsl.firmakurierska.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

 
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pojazd")
public class Pojazd {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idPojazdu;
	
	private String typPojazdu;
	private double pojemnosc;
	private String marka;
	private String model;
	@Column(unique = true)
	private String nrRejestr;
	
	@OneToMany(mappedBy = "pojazd", fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Dostawa> dostawy;

}