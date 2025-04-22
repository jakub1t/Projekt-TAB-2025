package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.model.Klient;
import com.polsl.firmakurierska.repository.KlientRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/klient")
public class KlientController {

	@Autowired
	KlientRepository klientRepository;
	
	@GetMapping("/{id}")
	public Optional<Klient> getKlientById(@PathVariable Integer id){
		return klientRepository.findById(id);
	}

	@GetMapping("/all")
	public List<Klient> getAllKlienci() {
	    return (List<Klient>) klientRepository.findAll();
	}
	
	@PostMapping
	public Klient addklient(@RequestBody Klient klient) {
		return klientRepository.save(klient);
	}
	
	@DeleteMapping("/{id}")
	public void deleteKlient (@PathVariable Integer id) {
		klientRepository.deleteById(id);
	}
	
	@GetMapping("/findByImie/{imie}")
	public List<Klient> getKlientByImie(@PathVariable String imie){
		return klientRepository.findByImieK(imie);
	}
	
	@GetMapping("/findByNazwisko/{nazwisko}")
	public List<Klient> getKlientByNazwisko(@PathVariable String nazwisko){
		return klientRepository.findByNazwiskoK(nazwisko);
	}
	
	@GetMapping("/findByImieAndNazwisko/{imie}/{nazwisko}")
    public List<Klient> getKlientByImieAndNazwisko(@PathVariable String imie, @PathVariable String nazwisko) {
        return klientRepository.findByImieKAndNazwiskoK(imie, nazwisko);
    }
	
	@GetMapping("/ping")
	public String ping() {
	    return "Działa!";
	}
}
