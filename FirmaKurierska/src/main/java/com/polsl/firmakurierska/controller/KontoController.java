package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.model.Konto;
import com.polsl.firmakurierska.repository.KontoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/konto")
public class KontoController {

    @Autowired
    KontoRepository kontoRepository;

    
    @PostMapping
    public Konto createKonto(@RequestBody Konto konto) {
        return kontoRepository.save(konto);
    }

    
    @GetMapping("/{login}")
    public Optional<Konto> getKontoByLogin(@PathVariable String login) {
        return kontoRepository.findByLogin(login);
    }


    @GetMapping("/all")
    public List<Konto> getAllKonto() {
        List<Konto> accounts = new ArrayList<>();
        kontoRepository.findAll().forEach(accounts::add);;

        return accounts;
    }

    
    @GetMapping("/exists/{login}")
    public boolean kontoExists(@PathVariable String login) {
        return kontoRepository.existsByLogin(login);
    }

    // Logowanie (login + hasło)
    @PostMapping("/login")
    public String login(@RequestBody Konto konto) {
        boolean correct = kontoRepository.findByLoginAndHaslo(konto.getLogin(), konto.getHaslo()).isPresent();
        return correct ? "Zalogowano pomyślnie" : "Błędny login lub hasło";
    }

    
    @DeleteMapping("/{login}")
    public void deleteKonto(@PathVariable String login) {
        kontoRepository.deleteByLogin(login);
    }
}
