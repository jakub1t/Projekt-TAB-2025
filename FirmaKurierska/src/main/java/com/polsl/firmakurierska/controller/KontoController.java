package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Konto;
import com.polsl.firmakurierska.repository.KontoRepository;

import jakarta.transaction.Transactional;

import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/konto")
public class KontoController {

    @Autowired
    KontoRepository kontoRepository;

    @GetMapping("/all")
    public List<Konto> getAllKonta() {
        List<Konto> accounts = new ArrayList<>();
        kontoRepository.findAll().forEach(accounts::add);;

        return accounts;
    }
  
    @GetMapping("/{id}")
    public Konto getKontoById(@PathVariable Integer id) {
        return kontoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Konto o ID " + id + " nie istnieje"));
    }

    @PostMapping("/getid")
    public String getKontoByLogin(@RequestParam String login) {
        // return kontoRepository.findByLogin(login)
        //         .orElseThrow(() -> new ResourceNotFoundException("Konto o loginie '" + login + "' nie istnieje"));
        Konto acc = kontoRepository.findByLogin(login).orElseThrow(
            () -> new ResourceNotFoundException("Konto o loginie '" + login + "' nie istnieje")
        );

        return acc.getIdKonta().toString();
    }

    @PostMapping("/login")
    public String loginp(@RequestBody Konto konto) {
        boolean correct = kontoRepository.findByLoginAndHaslo(konto.getLogin(), konto.getHaslo()).isPresent();
        return correct ? "Zalogowano pomyślnie" : "Błędny login lub hasło";
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

    @DeleteMapping("/delete/{acId}")
    @Transactional
    public void deleteKontoById(@PathVariable Integer acId) {
        if (!kontoRepository.existsById(acId)) {
            throw new ResourceNotFoundException("Konto o ID '" + acId + "' nie istnieje");   
        }
        kontoRepository.deleteById(acId);
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
