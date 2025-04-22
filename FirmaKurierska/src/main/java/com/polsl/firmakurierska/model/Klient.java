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
	
	@OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    @JsonIgnore
	private List<Paczka> paczki;
	
}