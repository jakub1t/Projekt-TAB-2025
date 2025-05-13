package com.polsl.firmakurierska.repository;

import com.polsl.firmakurierska.model.Pracownik;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface PracownikRepository extends CrudRepository<Pracownik, Integer> {
    
	List<Pracownik> findByImie(String imie);
	
	List<Pracownik> findByImieIgnoreCase(String imie);
	
	List<Pracownik> findByNazwisko(String nazwisko);
	
	Optional<Pracownik> findByPesel(String pesel);
	
	List<Pracownik> findByImieAndNazwisko(String imie, String nazwisko);
	
	List<Pracownik> findByImieOrNazwisko(String imie, String nazwisko);
	
	List<Pracownik> findByImieStartingWith(String prefix);
	
	List<Pracownik> findByNazwiskoContaining(String fragment);
	
	List<Pracownik> findByPeselGreaterThan(String pesel);
	
	List<Pracownik> findAllByOrderByNazwiskoAsc();
    List<Pracownik> findAllByOrderByNazwiskoDesc();
    
    boolean existsByPesel(String pesel);
    
    long countByNazwisko(String nazwisko);

	void deleteByPesel(String pesel);
}
