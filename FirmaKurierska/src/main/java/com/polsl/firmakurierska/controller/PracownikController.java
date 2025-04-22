package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.model.Pracownik;
import com.polsl.firmakurierska.model.PrawoJazdy;
import com.polsl.firmakurierska.repository.PracownikRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/pracownik")
public class PracownikController {

    @Autowired
    PracownikRepository pracownikRepository;

    @GetMapping("/{id}")
    public Optional<Pracownik> getPracownikById(@PathVariable Integer id) {
        return pracownikRepository.findById(id);
    }

    @GetMapping("/all")
    public List<Pracownik> getAllPracownicy() {
        return (List<Pracownik>) pracownikRepository.findAll();
    }

    @PostMapping
    public Pracownik addPracownik(@RequestBody Pracownik pracownik) {
        return pracownikRepository.save(pracownik);
    }

    @DeleteMapping("/{pesel}")
    public void deletePracownikByPesel(@PathVariable String pesel) {
        pracownikRepository.deleteByPesel(pesel);
    }

    @GetMapping("/imie/{imie}")
    public List<Pracownik> getPracownicyByImie(@PathVariable String imie) {
        return pracownikRepository.findByImie(imie);
    }

    @GetMapping("/nazwisko/{nazwisko}")
    public List<Pracownik> getPracownicyByNazwisko(@PathVariable String nazwisko) {
        return pracownikRepository.findByNazwisko(nazwisko);
    }

    @GetMapping("/imieNazwisko/{imie}/{nazwisko}")
    public List<Pracownik> getPracownicyByImieAndNazwisko(@PathVariable String imie, @PathVariable String nazwisko) {
        return pracownikRepository.findByImieAndNazwisko(imie, nazwisko);
    }

    @GetMapping("/pesel/{pesel}")
    public Optional<Pracownik> getPracownikByPesel(@PathVariable String pesel) {
        return pracownikRepository.findByPesel(pesel);
    }

    @GetMapping("/imieStartingWith/{prefix}")
    public List<Pracownik> getPracownicyByImieStartingWith(@PathVariable String prefix) {
        return pracownikRepository.findByImieStartingWith(prefix);
    }

    @GetMapping("/nazwiskoContaining/{fragment}")
    public List<Pracownik> getPracownicyByNazwiskoContaining(@PathVariable String fragment) {
        return pracownikRepository.findByNazwiskoContaining(fragment);
    }

    @GetMapping("/exists/{pesel}")
    public boolean existsByPesel(@PathVariable String pesel) {
        return pracownikRepository.existsByPesel(pesel);
    }

    @GetMapping("/count/{nazwisko}")
    public long countByNazwisko(@PathVariable String nazwisko) {
        return pracownikRepository.countByNazwisko(nazwisko);
    }

    @GetMapping("/allSortedAsc")
    public List<Pracownik> getAllPracownicySortedAsc() {
        return pracownikRepository.findAllByOrderByNazwiskoAsc();
    }

    @GetMapping("/allSortedDesc")
    public List<Pracownik> getAllPracownicySortedDesc() {
        return pracownikRepository.findAllByOrderByNazwiskoDesc();
    }
    
    @Transactional
    //zarządza transakcją bazy danych – czyli zapewnia, że operacje wykonywane wewnątrz metody są wykonywane jako jedna całość
    @GetMapping("/{id}/prawa-jazdy")
    public ResponseEntity<Set<PrawoJazdy>> getPrawaJazdyByPracownikId(@PathVariable Integer id) {
        Optional<Pracownik> pracownikOptional = pracownikRepository.findById(id);

        if (pracownikOptional.isPresent()) {
            Pracownik pracownik = pracownikOptional.get();
            Set<PrawoJazdy> prawaJazdy = pracownik.getPrawoJazdy();
            return ResponseEntity.ok(prawaJazdy);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}