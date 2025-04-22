package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.model.Produkt;
import com.polsl.firmakurierska.repository.ProduktRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/produkt")
public class ProduktController {

    @Autowired
    private ProduktRepository produktRepository;

    @GetMapping("/{id}")
    public Optional<Produkt> getProduktById(@PathVariable Integer id) {
        return produktRepository.findById(id);
    }

    @GetMapping("/all")
    public List<Produkt> getAllProdukty() {
        return (List<Produkt>) produktRepository.findAll();
    }

    @PostMapping
    public Produkt addProdukt(@RequestBody Produkt produkt) {
        return produktRepository.save(produkt);
    }

    @DeleteMapping("/nrSeryjny/{nrSeryjny}")
    public void deleteProdukt(@PathVariable String nrSeryjny) {
        produktRepository.deleteByNrSeryjny(nrSeryjny);
    }

    @GetMapping("/kategoria/{kategoriaProd}")
    public List<Produkt> getByKategoriaProd(@PathVariable String kategoriaProd) {
        return produktRepository.findByKategoriaProd(kategoriaProd);
    }

    @GetMapping("/nazwa/{nazwaProduktu}")
    public List<Produkt> getByNazwaProduktu(@PathVariable String nazwaProduktu) {
        return produktRepository.findByNazwaProduktu(nazwaProduktu);
    }

    @GetMapping("/wagaGreaterThan/{waga}")
    public List<Produkt> getByWagaGreaterThan(@PathVariable double waga) {
        return produktRepository.findByWagaGreaterThan(waga);
    }

    @GetMapping("/wagaLessThan/{waga}")
    public List<Produkt> getByWagaLessThan(@PathVariable double waga) {
        return produktRepository.findByWagaLessThan(waga);
    }

    @GetMapping("/wagaBetween/{start}/{end}")
    public List<Produkt> getByWagaBetween(@PathVariable double start, @PathVariable double end) {
        return produktRepository.findByWagaBetween(start, end);
    }

    @GetMapping("/exists/{nrSeryjny}")
    public boolean existsByNrSeryjny(@PathVariable String nrSeryjny) {
        return produktRepository.existsByNrSeryjny(nrSeryjny);
    }

    @GetMapping("/count/kategoria/{kategoriaProd}")
    public long countByKategoriaProd(@PathVariable String kategoriaProd) {
        return produktRepository.countByKategoriaProd(kategoriaProd);
    }

    @GetMapping("/count/nazwa/{nazwaProduktu}")
    public long countByNazwaProduktu(@PathVariable String nazwaProduktu) {
        return produktRepository.countByNazwaProduktu(nazwaProduktu);
    }
}
