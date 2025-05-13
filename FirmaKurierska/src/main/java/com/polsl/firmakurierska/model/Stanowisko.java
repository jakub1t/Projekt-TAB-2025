package com.polsl.firmakurierska.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

 
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stanowisko")
public class Stanowisko {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idStanowisko;
	
	@Column(name = "nazwaStanowiska")
	private String nazwaStanowiska;
  
	@OneToMany(mappedBy = "stanowisko", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
	private List<Pracownik> pracownik;
	}