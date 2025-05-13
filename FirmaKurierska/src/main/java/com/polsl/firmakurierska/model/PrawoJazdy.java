package com.polsl.firmakurierska.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

 
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "pracownicy")
@Table(name = "prawojazdy")
public class PrawoJazdy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idKat;
	
	private String kategoria;
	
	@ManyToMany(mappedBy = "prawoJazdy", fetch = FetchType.EAGER)
	@JsonIgnore
    private Set<Pracownik> pracownicy;
}