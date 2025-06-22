package com.polsl.firmakurierska.model;

import jakarta.persistence.*;
import lombok.*;

 
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "produkt")
public class Produkt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idProduktu;
	
	@Column(unique = true)
	private String nrSeryjny;
	private String kategoriaProd;
	private String nazwaProduktu;
	private double waga;
	
	@ManyToOne
	   @JoinColumn(name = "fk_paczka_id", nullable = true)  
	   private Paczka paczka;
	
	@ManyToOne
	   @JoinColumn(name = "fk_producent_id") 
	   private Producent producent;
	
}