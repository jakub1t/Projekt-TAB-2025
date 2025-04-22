package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.model.Producent;
import com.polsl.firmakurierska.repository.ProducentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/producent")
public class ProducentController {

    @Autowired
    ProducentRepository producentRepository;

    @GetMapping("/{id}")
    public Optional<Producent> getProducentById(@PathVariable Integer id) {
        return producentRepository.findById(id);
    }

    @GetMapping("/all")
    public List<Producent> getAllProducents() {
        return (List<Producent>) producentRepository.findAll();
    }

    @PostMapping
    public Producent addProducent(@RequestBody Producent producent) {
        return producentRepository.save(producent);
    }

    @DeleteMapping("/{id}")
    public void deleteProducent(@PathVariable Integer id) {
        producentRepository.deleteById(id);
    }

    @GetMapping("/nazwa/{nazwaProducenta}")
    public List<Producent> getByNazwaProducenta(@PathVariable String nazwaProducenta) {
        return producentRepository.findByNazwaProducenta(nazwaProducenta);
    }

    @GetMapping("/count/{nazwaProducenta}")
    public long countByNazwaProducenta(@PathVariable String nazwaProducenta) {
        return producentRepository.countByNazwaProducenta(nazwaProducenta);
    }

    @GetMapping("/exists/{nazwaProducenta}")
    public boolean existsByNazwaProducenta(@PathVariable String nazwaProducenta) {
        return producentRepository.existsByNazwaProducenta(nazwaProducenta);
    }

}
