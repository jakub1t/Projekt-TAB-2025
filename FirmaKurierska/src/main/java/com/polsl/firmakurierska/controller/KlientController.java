package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Klient;
import com.polsl.firmakurierska.repository.KlientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/klient")
public class KlientController {

	@Autowired
	KlientRepository klientRepository;
	
	@GetMapping("/{id}")
    public Klient getKlientById(@PathVariable Integer id) {
		return klientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Klient o ID " + id + " nie istnieje"));
    }

	@GetMapping
	public List<Klient> getAllKlienci() {
	    return (List<Klient>) klientRepository.findAll();
	}
	
	@PostMapping
	public Klient addklient(@RequestBody Klient klient) {
		if(klient.getImieK() == null || klient.getNazwiskoK() == null) {
			throw new BadRequestException("Imię i nazwisko klienta są wymagane.");
		}
		return klientRepository.save(klient);
	}
	
	@DeleteMapping("/{id}")
	public void deleteKlient (@PathVariable Integer id) {
		if(!klientRepository.existsById(id)) {
			throw new ResourceNotFoundException("Klient o ID " + id + " nie istnieje");
		}
		klientRepository.deleteById(id);
	}
	
	@GetMapping("/szukaj")
	public List<Klient> getKlientByParameters(
	        @RequestParam(required = false) String imiek, 
	        @RequestParam(required = false) String nazwiskok
	) {
	    if ((imiek == null || imiek.isEmpty()) && (nazwiskok == null || nazwiskok.isEmpty())) {
	        throw new BadRequestException("Musisz podać przynajmniej jedno kryterium: imię lub nazwisko.");
	    }

	    if (imiek != null && !imiek.isEmpty()) {
	        if (Character.isLowerCase(imiek.charAt(0))) {
	            throw new BadRequestException("Imię musi zaczynać się od dużej litery.");
	        }
	        imiek = imiek.toLowerCase();  
	        List<Klient> list = klientRepository.findByImieKIgnoreCase(imiek);
	        if (list.isEmpty()) {
	            throw new ResourceNotFoundException("Nie znaleziono klientów o imieniu: " + imiek);
	        }
	        return list;
	    }

	    if (nazwiskok != null && !nazwiskok.isEmpty()) {
	        if (Character.isLowerCase(nazwiskok.charAt(0))) {
	            throw new BadRequestException("Nazwisko musi zaczynać się od dużej litery.");
	        }
	        nazwiskok = nazwiskok.toLowerCase();  // Zamiana nazwiska na małe litery
	        List<Klient> list = klientRepository.findByNazwiskoKIgnoreCase(nazwiskok);
	        if (list.isEmpty()) {
	            throw new ResourceNotFoundException("Nie znaleziono klientów o nazwisku: " + nazwiskok);
	        }
	        return list;
	    }

	    imiek = imiek != null ? imiek.toLowerCase() : null;
	    nazwiskok = nazwiskok != null ? nazwiskok.toLowerCase() : null;
	    List<Klient> list = klientRepository.findByImieKAndNazwiskoKIgnoreCase(imiek, nazwiskok); 
	    if (list.isEmpty()) {
	        throw new ResourceNotFoundException("Nie znaleziono klientów o imieniu " + imiek + " i nazwisku " + nazwiskok);
	    }

	    return list;
	}

	
	@PutMapping("/{id}")
	public Klient updateKlient(@PathVariable Integer id, @RequestBody Klient newData) {
		if(!klientRepository.existsById(id)) {
			throw new ResourceNotFoundException("Klient o ID " + id + " nie istnieje.");
		}
	    return klientRepository.findById(id).map(klient -> {
	    	if(newData.getImieK() == null || newData.getNazwiskoK() == null) {
	    		throw new BadRequestException("Imię i nazwisko klienta są wymagane.");
	    	}
	        klient.setImieK(newData.getImieK());
	        klient.setNazwiskoK(newData.getNazwiskoK());
	        return klientRepository.save(klient);
	    }).orElseThrow(() -> new ResourceNotFoundException("Klient o ID " + id + " nie istnieje"));
	}
}
