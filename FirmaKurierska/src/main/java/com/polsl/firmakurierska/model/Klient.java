package com.polsl.firmakurierska.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

 
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "klient")
public class Klient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idKlienta;
	
	private String imieK;
	private String nazwiskoK;
	
	//OK, bo klient tworzy paczki, może mieć wiele paczek, a usunięcie klienta może usunąć paczki.
	@OneToMany(mappedBy = "klient", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Paczka> paczki;
	
}