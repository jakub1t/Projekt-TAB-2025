package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Producent;
import com.polsl.firmakurierska.repository.ProducentRepository;
import com.polsl.firmakurierska.repository.ProduktRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/producent")
public class ProducentController {

    @Autowired
    ProducentRepository producentRepository;
    
    @Autowired
    ProduktRepository produktRepository;
    
    @GetMapping("/{id}")
    public Producent getProducentById(@PathVariable Integer id) {
        Producent producent = producentRepository.findById(id).orElse(null);
        if (producent == null) {
            throw new ResourceNotFoundException("Producent o ID " + id + " nie znaleziony");
        }
        return producent;
    }

    @GetMapping
    public List<Producent> getAllProducents() {
        List<Producent> producents = (List<Producent>) producentRepository.findAll();
        if (producents.isEmpty()) {
            throw new ResourceNotFoundException("Brak producentów w bazie.");
        }
        return producents;
    }

    @PostMapping
    public Producent addProducent(@RequestBody Producent producent) {
        if (producent.getNazwaProducenta() == null || producent.getNazwaProducenta().isEmpty()) {
            throw new BadRequestException("Nazwa producenta jest wymagana.");
        }
        return producentRepository.save(producent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducent(@PathVariable Integer id) {
        Producent producent = producentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producent o ID " + id + " nie istnieje."));

        boolean powiazaneProdukty = produktRepository.existsByProducent_IdProducenta(id);
        if (powiazaneProdukty) {
            throw new BadRequestException("Nie można usunąć producenta, ponieważ istnieją produkty przypisane do niego.");
        }

        producentRepository.delete(producent);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/nazwa")
    public List<Producent> getByNazwaProducenta(@RequestParam String nazwaProducenta) {
        List<Producent> producents = producentRepository.findByNazwaProducenta(nazwaProducenta);
        if (producents.isEmpty()) {
            throw new ResourceNotFoundException("Brak producentów o nazwie: " + nazwaProducenta);
        }
        return producents;
    }

    @GetMapping("/count/{nazwaProducenta}")
    public long countByNazwaProducenta(@PathVariable String nazwaProducenta) {
        long count = producentRepository.countByNazwaProducenta(nazwaProducenta);
        if (count == 0) {
            throw new ResourceNotFoundException("Brak producentów o nazwie: " + nazwaProducenta);
        }
        return count;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producent> updateProducent(@PathVariable Integer id, @RequestBody Producent producent) {
        if (!producentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producent o ID " + id + " nie istnieje.");
        }
        producent.setIdProducenta(id); 
        Producent updatedProducent = producentRepository.save(producent);
        return new ResponseEntity<>(updatedProducent, HttpStatus.OK);
    }

}
