package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.model.Pojazd;
import com.polsl.firmakurierska.repository.PojazdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pojazd")
public class PojazdController {

    @Autowired
    PojazdRepository pojazdRepository;

    @GetMapping("/all")
    public Iterable<Pojazd> getAll() {
        return pojazdRepository.findAll();
    }

    @PostMapping
    public Pojazd add(@RequestBody Pojazd pojazd) {
        return pojazdRepository.save(pojazd);
    }

    @GetMapping("/{id}")
    public Optional<Pojazd> getById(@PathVariable Integer id) {
        return pojazdRepository.findById(id);
    }

    @PutMapping("/{id}")
    public Pojazd update(@PathVariable Integer id, @RequestBody Pojazd updated) {
        Pojazd pojazd = pojazdRepository.findById(id).orElseThrow();
        pojazd.setTypPojazdu(updated.getTypPojazdu());
        pojazd.setPojemnosc(updated.getPojemnosc());
        pojazd.setMarka(updated.getMarka());
        pojazd.setModel(updated.getModel());
        pojazd.setNrRejestr(updated.getNrRejestr());
        return pojazdRepository.save(pojazd);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        pojazdRepository.deleteById(id);
    }

    @GetMapping("/typ/{typ}")
    public List<Pojazd> getByTyp(@PathVariable String typ) {
        return pojazdRepository.findByTypPojazdu(typ);
    }

    @GetMapping("/marka/{marka}")
    public List<Pojazd> getByMarka(@PathVariable String marka) {
        return pojazdRepository.findByMarka(marka);
    }

    @GetMapping("/model/{model}")
    public List<Pojazd> getByModel(@PathVariable String model) {
        return pojazdRepository.findByModel(model);
    }

    @GetMapping("/rejestr/{nr}")
    public Optional<Pojazd> getByNrRejestr(@PathVariable String nr) {
        return pojazdRepository.findByNrRejestr(nr);
    }

    @GetMapping("/pojemnosc/greater/{value}")
    public List<Pojazd> getByPojemnoscGreater(@PathVariable double value) {
        return pojazdRepository.findByPojemnoscGreaterThan(value);
    }

    @GetMapping("/pojemnosc/less/{value}")
    public List<Pojazd> getByPojemnoscLess(@PathVariable double value) {
        return pojazdRepository.findByPojemnoscLessThan(value);
    }

    @GetMapping("/pojemnosc/between/{min}/{max}")
    public List<Pojazd> getByPojemnoscBetween(@PathVariable double min, @PathVariable double max) {
        return pojazdRepository.findByPojemnoscBetween(min, max);
    }

    @GetMapping("/exists/{nr}")
    public boolean existsByNr(@PathVariable String nr) {
        return pojazdRepository.existsByNrRejestr(nr);
    }

    @GetMapping("/count/marka/{marka}")
    public long countByMarka(@PathVariable String marka) {
        return pojazdRepository.countByMarka(marka);
    }

    @GetMapping("/count/typ/{typ}")
    public long countByTyp(@PathVariable String typ) {
        return pojazdRepository.countByTypPojazdu(typ);
    }

    @DeleteMapping("/rejestr/{nr}")
    public void deleteByNrRejestr(@PathVariable String nr) {
        pojazdRepository.deleteByNrRejestr(nr);
    }
}
