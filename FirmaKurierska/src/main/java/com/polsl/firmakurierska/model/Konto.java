package com.polsl.firmakurierska.model;

import jakarta.persistence.*;
import lombok.*;

 
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "konto")
public class Konto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idKonta;
	
	@Column(unique = true)
	private String login;
	@Column(unique = true)
	private String haslo;
}