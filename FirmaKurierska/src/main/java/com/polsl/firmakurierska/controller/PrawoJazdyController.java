package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.dto.PrawoJazdyDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.PrawoJazdy;
import com.polsl.firmakurierska.repository.PrawoJazdyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/prawojazdy")
public class PrawoJazdyController {

    @Autowired
    PrawoJazdyRepository prawoJazdyRepository;

    @PostMapping
    public ResponseEntity<PrawoJazdy> createPrawoJazdy(@RequestBody PrawoJazdy prawoJazdy) {
    	if(prawoJazdy.getKategoria() == null || prawoJazdy.getKategoria().isBlank()) {
    		throw new BadRequestException("Kategoria prawa jazdy nie może być pusta.");
    	}
        PrawoJazdy savedPrawoJazdy = prawoJazdyRepository.save(prawoJazdy);
        return new ResponseEntity<>(savedPrawoJazdy, HttpStatus.CREATED);
    }
    
    @GetMapping
    public @ResponseBody Iterable<PrawoJazdyDTO> getAllPrawoJazdy() {
        return StreamSupport.stream(prawoJazdyRepository.findAll().spliterator(), false)
                .map(PrawoJazdyDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrawoJazdyDTO> getPrawoJazdyById(@PathVariable String id) {
        Integer parsedId;
        try {
            parsedId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Nieprawidłowy format ID: '" + id + "'. Wymagana liczba całkowita.");
        }

        PrawoJazdy prawoJazdy = prawoJazdyRepository.findById(parsedId)
                .orElseThrow(() -> new ResourceNotFoundException("Prawo jazdy o ID " + parsedId + " nie istnieje"));

        return ResponseEntity.ok(new PrawoJazdyDTO(prawoJazdy));
    }

    @GetMapping("/kategoria")
    public List<PrawoJazdy> getPrawoJazdyByKategoria(@RequestParam String kategoria) {
    	if(!prawoJazdyRepository.existsByKategoria(kategoria) ) {
    		throw new ResourceNotFoundException("Prawo jazdy o kategorii " + kategoria + " nie istnieje");
    	}
        return prawoJazdyRepository.findByKategoria(kategoria);
    }

    @GetMapping("/count")
    public long countByKategoria(@RequestParam String kategoria) {
    	if(!prawoJazdyRepository.existsByKategoria(kategoria) ) {
    		throw new ResourceNotFoundException("Prawo jazdy o kategorii " + kategoria + " nie istnieje");
    	}
        return prawoJazdyRepository.countByKategoria(kategoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrawoJazdy> updatePrawoJazdy(@PathVariable Integer id, @RequestBody PrawoJazdy prawoJazdy) {
        if (!prawoJazdyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prawo jazdy o ID " + id + " nie istnieje");
        }

        if (prawoJazdy.getKategoria() == null || prawoJazdy.getKategoria().isBlank()) {
            throw new BadRequestException("Kategoria prawa jazdy nie może być pusta.");
        }

        prawoJazdy.setIdKat(id);
        PrawoJazdy updated = prawoJazdyRepository.save(prawoJazdy);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrawoJazdy(@PathVariable Integer id) {
        if (!prawoJazdyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prawo jazdy o ID " + id + " nie istnieje");
        }

        prawoJazdyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
