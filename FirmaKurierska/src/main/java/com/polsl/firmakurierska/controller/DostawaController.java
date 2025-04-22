package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.model.Dostawa;
import com.polsl.firmakurierska.repository.DostawaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dostawa")
public class DostawaController {

    @Autowired
    DostawaRepository dostawaRepository;
 
    @Transactional
    @GetMapping("/{id}")
    public Dostawa getDostawaById(@PathVariable Integer id) {
        return dostawaRepository.findById(id).orElse(null);
    }

    @GetMapping("/all")
    public List<Dostawa> getAllDostawy() {
        return (List<Dostawa>) dostawaRepository.findAll();
    }

    @PostMapping
    public Dostawa addDostawa(@RequestBody Dostawa dostawa) {
        return dostawaRepository.save(dostawa);
    }

    @DeleteMapping("/{id}")
    public void deleteDostawa(@PathVariable Integer id) {
        dostawaRepository.deleteById(id);
    }

    @GetMapping("/punktA/{punktA}")
    public List<Dostawa> getByPunktA(@PathVariable String punktA) {
        return dostawaRepository.findByPunktA(punktA);
    }

    @GetMapping("/punktB/{punktB}")
    public List<Dostawa> getByPunktB(@PathVariable String punktB) {
        return dostawaRepository.findByPunktB(punktB);
    }

    @GetMapping("/dataWyruszenia/{data}")
    public List<Dostawa> getByDataWyruszenia(@PathVariable String data) {
        return dostawaRepository.findByDataWyruszenia(LocalDate.parse(data));
    }

    @GetMapping("/termin/{data}")
    public List<Dostawa> getByTermin(@PathVariable String data) {
        return dostawaRepository.findByTermin(LocalDate.parse(data));
    }

    @GetMapping("/dataPrzed/{data}")
    public List<Dostawa> getByDataWyruszeniaBefore(@PathVariable String data) {
        return dostawaRepository.findByDataWyruszeniaBefore(LocalDate.parse(data));
    }

    @GetMapping("/terminPo/{data}")
    public List<Dostawa> getByTerminAfter(@PathVariable String data) {
        return dostawaRepository.findByTerminAfter(LocalDate.parse(data));
    }

    @GetMapping("/dataZakres/{start}/{end}")
    public List<Dostawa> getByDataWyruszeniaBetween(@PathVariable String start, @PathVariable String end) {
        return dostawaRepository.findByDataWyruszeniaBetween(LocalDate.parse(start), LocalDate.parse(end));
    }

    @GetMapping("/trasa/{punktA}/{punktB}")
    public List<Dostawa> getByPunktAAndPunktB(@PathVariable String punktA, @PathVariable String punktB) {
        return dostawaRepository.findByPunktAAndPunktB(punktA, punktB);
    }

    @GetMapping("/terminAsc")
    public List<Dostawa> getAllOrderedByTerminAsc() {
        return dostawaRepository.findAllByOrderByTerminAsc();
    }

    @GetMapping("/terminDesc")
    public List<Dostawa> getAllOrderedByTerminDesc() {
        return dostawaRepository.findAllByOrderByTerminDesc();
    }
}
