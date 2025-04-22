package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.model.PrawoJazdy;
import com.polsl.firmakurierska.repository.PrawoJazdyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/prawajazdy")
public class PrawoJazdyController {

    @Autowired
    PrawoJazdyRepository prawoJazdyRepository;

    @PostMapping
    public ResponseEntity<PrawoJazdy> createPrawoJazdy(@RequestBody PrawoJazdy prawoJazdy) {
        PrawoJazdy savedPrawoJazdy = prawoJazdyRepository.save(prawoJazdy);
        return new ResponseEntity<>(savedPrawoJazdy, HttpStatus.CREATED);
    }

    @GetMapping
    public List<PrawoJazdy> getAllPrawoJazdy() {
        return (List<PrawoJazdy>) prawoJazdyRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrawoJazdy> getPrawoJazdyById(@PathVariable Integer id) {
        Optional<PrawoJazdy> prawoJazdy = prawoJazdyRepository.findById(id);
        if (prawoJazdy.isPresent()) {
            return new ResponseEntity<>(prawoJazdy.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/kategoria/{kategoria}")
    public List<PrawoJazdy> getPrawoJazdyByKategoria(@PathVariable String kategoria) {
        return prawoJazdyRepository.findByKategoria(kategoria);
    }

    @GetMapping("/count/{kategoria}")
    public long countByKategoria(@PathVariable String kategoria) {
        return prawoJazdyRepository.countByKategoria(kategoria);
    }

    @GetMapping("/exists/{kategoria}")
    public boolean existsByKategoria(@PathVariable String kategoria) {
        return prawoJazdyRepository.existsByKategoria(kategoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrawoJazdy> updatePrawoJazdy(@PathVariable Integer id, @RequestBody PrawoJazdy prawoJazdy) {
        if (prawoJazdyRepository.existsById(id)) {
            prawoJazdy.setIdKaty(id);
            PrawoJazdy updatedPrawoJazdy = prawoJazdyRepository.save(prawoJazdy);
            return new ResponseEntity<>(updatedPrawoJazdy, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrawoJazdy(@PathVariable Integer id) {
        if (prawoJazdyRepository.existsById(id)) {
            prawoJazdyRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
