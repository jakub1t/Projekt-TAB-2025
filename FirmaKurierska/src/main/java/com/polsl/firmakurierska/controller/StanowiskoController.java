package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Stanowisko;
import com.polsl.firmakurierska.repository.StanowiskoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stanowisko")
public class StanowiskoController {

    @Autowired
    StanowiskoRepository stanowiskoRepository;

    @GetMapping("/{id}")
    public Stanowisko getStanowiskoById(@PathVariable Integer id) {
        return stanowiskoRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono stanowiska o ID: " + id));
    }

	@GetMapping
	public List<Stanowisko> getAllStanowiska() {
	    List<Stanowisko> stanowiska = (List<Stanowisko>) stanowiskoRepository.findAll();
	    if (stanowiska.isEmpty()) {
	    	throw new ResourceNotFoundException("Brak dostępnych stanowisk.");
	    }
	    return stanowiska;
	}
	
    @PostMapping
    public Stanowisko addStanowisko(@RequestBody Stanowisko stanowisko) {
    	if(stanowisko == null || stanowisko.getNazwaStanowiska() == null || stanowisko.getNazwaStanowiska().isBlank()) {
    		throw new BadRequestException("Nieprawidłowe dane stanowiska.");
    	}
        return stanowiskoRepository.save(stanowisko);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStanowisko(@PathVariable Integer id) {
        Stanowisko stanowisko = stanowiskoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Stanowisko o ID " + id + " nie istnieje"));

        if (!stanowisko.getPracownik().isEmpty()) {
            throw new BadRequestException("Nie można usunąć stanowiska, ponieważ jest przypisane do pracownika.");
        }

        stanowiskoRepository.delete(stanowisko);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/szukaj")
    public List<Stanowisko> getStanowiskaByNazwa(@RequestParam String nazwa) {
    	if(nazwa == null || nazwa.isBlank()) {
    		throw new BadRequestException("Parametr 'nazwa' nie może być pusty.");
    	}
    	
    	List<Stanowisko> stanowisko_nazwa = (List<Stanowisko>) stanowiskoRepository.findByNazwaStanowiska(nazwa);
    	if(stanowisko_nazwa.isEmpty()) {
    		throw new ResourceNotFoundException("Nie znaleziono stanowisk o nazwie: " + nazwa);
    	}
        return stanowisko_nazwa;
    }

    @GetMapping("/count/{nazwa}")
    public long countByNazwa(@PathVariable String nazwa) {
        return stanowiskoRepository.countByNazwaStanowiska(nazwa);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Stanowisko> updateStanowisko(@PathVariable Integer id, @RequestBody Stanowisko stanowisko) {
      if(!stanowiskoRepository.existsById(id)) {
    	  throw new ResourceNotFoundException("Stanowisko o ID " + id + " nie istnieje.");
      }
      if(stanowisko == null || stanowisko.getNazwaStanowiska() == null || stanowisko.getNazwaStanowiska().isBlank()) {
    	  throw new BadRequestException("Nieprawidłowe dane wejściowe.");
      }
      
      stanowisko.setIdStanowisko(id);
      Stanowisko update = stanowiskoRepository.save(stanowisko);
      return ResponseEntity.ok(update);
    }

    
}
