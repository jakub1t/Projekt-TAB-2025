package com.polsl.firmakurierska.model; 
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

// automatycznie generować gettery i settery 
import jakarta.persistence.*; 
import lombok.*; 
@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@EqualsAndHashCode(exclude = "prawoJazdy")
@Table(name = "pracownik")
public class Pracownik { 
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer idOsoby; 
	private String imie; 
	private String nazwisko; 
	@Column(unique = true) 
	private String pesel; 
	//////////////////  
	@ManyToOne(cascade = CascadeType.ALL)
	
	   @JoinColumn(name = "stanowisko_id")  
	   private Stanowisko stanowisko;
	
	@OneToOne
    @JoinColumn(name = "konto_id", unique = true)
    private Konto konto;
	
	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable( //tworzy posredniczą tabelę
        name = "pracownik_prawojazdy",
        joinColumns = @JoinColumn(name = "pracownik_id"),
        inverseJoinColumns = @JoinColumn(name = "prawojazdy_id")
    )
    private Set<PrawoJazdy> prawoJazdy;
	
	
	@OneToMany(mappedBy = "pracownik", fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Dostawa> dostawy;

}