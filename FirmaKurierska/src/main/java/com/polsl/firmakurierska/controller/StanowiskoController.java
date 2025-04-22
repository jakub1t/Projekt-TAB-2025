package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.model.Stanowisko;
import com.polsl.firmakurierska.repository.StanowiskoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stanowisko")
public class StanowiskoController {

    @Autowired
    StanowiskoRepository stanowiskoRepository;

    // Pobieranie stanowiska po id
    @GetMapping("/{id}")
    public Optional<Stanowisko> getStanowiskoById(@PathVariable Integer id) {
        return stanowiskoRepository.findById(id);
    }

    // Pobieranie wszystkich stanowisk
    @GetMapping("/all")
    public List<Stanowisko> getAllStanowiska() {
        return (List<Stanowisko>) stanowiskoRepository.findAll();
    }

    // Dodawanie nowego stanowiska
    @PostMapping
    public Stanowisko addStanowisko(@RequestBody Stanowisko stanowisko) {
        return stanowiskoRepository.save(stanowisko);
    }

    // Usuwanie stanowiska po id
    @DeleteMapping("/{id}")
    public void deleteStanowisko(@PathVariable Integer id) {
        stanowiskoRepository.deleteById(id);
    }

    // Wyszukiwanie stanowiska po nazwie
    @GetMapping("/nazwa/{nazwa}")
    public List<Stanowisko> getStanowiskaByNazwa(@PathVariable String nazwa) {
        return stanowiskoRepository.findByNazwaStanowiska(nazwa);
    }

    // Sprawdzanie czy stanowisko o danej nazwie istnieje
    @GetMapping("/exists/{nazwa}")
    public boolean existsByNazwa(@PathVariable String nazwa) {
        return stanowiskoRepository.existsByNazwaStanowiska(nazwa);
    }

    // Liczenie stanowisk o danej nazwie
    @GetMapping("/count/{nazwa}")
    public long countByNazwa(@PathVariable String nazwa) {
        return stanowiskoRepository.countByNazwaStanowiska(nazwa);
    }
}
