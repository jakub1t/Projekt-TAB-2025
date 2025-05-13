package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Konto;
import com.polsl.firmakurierska.repository.KontoRepository;

import jakarta.transaction.Transactional;

import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/konto")
public class KontoController {

    @Autowired
    KontoRepository kontoRepository;

    @GetMapping
    public Iterable<Konto> getAllKonta() {
        return kontoRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Konto getKontoById(@PathVariable Integer id) {
        return kontoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Konto o ID " + id + " nie istnieje"));
    }

    @GetMapping("/login")
    public Konto getKontoByLogin(@RequestParam String login) {
        return kontoRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("Konto o loginie '" + login + "' nie istnieje"));
    }

    @PostMapping
    public Konto login(@RequestBody Konto konto) {
        if (konto.getLogin() == null || konto.getHaslo() == null) {
            throw new BadRequestException("Login i hasło nie mogą być puste");
        }

        if (kontoRepository.existsByHaslo(konto.getHaslo())) {
            throw new BadRequestException("To hasło jest już używane. Wybierz inne.");
        }

        return kontoRepository.save(konto);
    }

    @DeleteMapping("/login")
    @Transactional
    public void deleteKonto(@RequestParam String login) {
        if (!kontoRepository.existsByLogin(login)) {
            throw new ResourceNotFoundException("Konto o loginie '" + login + "' nie istnieje");
        }
        kontoRepository.deleteByLogin(login);
    }

    @PutMapping("/update/{login}")
    public Konto updateKonto(@PathVariable String login, @RequestBody Konto newData) {
        if (newData.getHaslo() == null || newData.getHaslo().isEmpty()) {
            throw new BadRequestException("Hasło nie może być puste");
        }

        kontoRepository.findByLogin(login).orElseThrow(() ->
                new ResourceNotFoundException("Konto o loginie '" + login + "' nie istnieje"));

        boolean hasloZajete = StreamSupport.stream(kontoRepository.findAll().spliterator(), false)
                .anyMatch(k -> k.getHaslo().equals(newData.getHaslo()) && !k.getLogin().equals(login));

        if (hasloZajete) {
            throw new BadRequestException("To hasło jest już używane przez inne konto.");
        }

        return kontoRepository.findByLogin(login)
                .map(konto -> {
                    konto.setHaslo(newData.getHaslo());
                    return kontoRepository.save(konto);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Konto o loginie '" + login + "' nie istnieje"));
    }

}
