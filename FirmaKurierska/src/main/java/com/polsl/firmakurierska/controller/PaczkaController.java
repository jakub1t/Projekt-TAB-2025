package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.model.Paczka;
import com.polsl.firmakurierska.repository.PaczkaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/paczka")
public class PaczkaController {

    @Autowired
    PaczkaRepository paczkaRepository;
    
    @GetMapping("/all")
    public Iterable<Paczka> getAllPaczki() {
        return paczkaRepository.findAll();
    }

    //proba zapisu
    @PostMapping("/savePaczka")
    public ResponseEntity<String> savePaczka(@RequestBody List<Paczka> paczkaData){
    	paczkaRepository.saveAll(paczkaData);
    	return ResponseEntity.ok("Data saved");
    }
    
    @PostMapping
    public Paczka addPaczka(@RequestBody Paczka paczka) {
        return paczkaRepository.save(paczka);
    }

    @GetMapping("/{id}")
    public Optional<Paczka> getById(@PathVariable Integer id) {
        return paczkaRepository.findById(id);
    }

    @PutMapping("/{id}")
    public Paczka updatePaczka(@PathVariable Integer id, @RequestBody Paczka paczkaDetails) {
        Paczka paczka = paczkaRepository.findById(id).orElseThrow();
        paczka.setWagaPaczki(paczkaDetails.getWagaPaczki());
        return paczkaRepository.save(paczka);
    }

    @DeleteMapping("/{id}")
    public void deletePaczka(@PathVariable Integer id) {
        paczkaRepository.deleteById(id);
    }

    @GetMapping("/waga/{waga}")
    public List<Paczka> getByWaga(@PathVariable double waga) {
        return paczkaRepository.findByWagaPaczki(waga);
    }

    @GetMapping("/waga/greater/{waga}")
    public List<Paczka> getByWagaGreater(@PathVariable double waga) {
        return paczkaRepository.findByWagaPaczkiGreaterThan(waga);
    }

    @GetMapping("/waga/less/{waga}")
    public List<Paczka> getByWagaLess(@PathVariable double waga) {
        return paczkaRepository.findByWagaPaczkiLessThan(waga);
    }

    @GetMapping("/waga/between/{min}/{max}")
    public List<Paczka> getByWagaRange(@PathVariable double min, @PathVariable double max) {
        return paczkaRepository.findByWagaPaczkiBetween(min, max);
    }

    @GetMapping("/exists/{waga}")
    public boolean exists(@PathVariable double waga) {
        return paczkaRepository.existsByWagaPaczki(waga);
    }

    @GetMapping("/count/{waga}")
    public long count(@PathVariable double waga) {
        return paczkaRepository.countByWagaPaczki(waga);
    }
    
    
    @GetMapping("/{id}/dostawa")
    public ResponseEntity<?> getDostawaForPaczka(@PathVariable Integer id) {
        Optional<Paczka> paczkaOptional = paczkaRepository.findById(id);
        if (paczkaOptional.isPresent()) {
            Paczka paczka = paczkaOptional.get();
            return ResponseEntity.ok(paczka.getDostawa());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
