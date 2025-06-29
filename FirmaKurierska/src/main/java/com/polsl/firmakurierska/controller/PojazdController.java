package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Dostawa;
import com.polsl.firmakurierska.model.Pojazd;
import com.polsl.firmakurierska.repository.PojazdRepository;

import jakarta.transaction.Transactional;

import com.polsl.firmakurierska.repository.DostawaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/pojazd")
public class PojazdController {

    @Autowired
    PojazdRepository pojazdRepository;
    
    @Autowired
    DostawaRepository dostawaRepository;

    @GetMapping
    public Iterable<Pojazd> getAll() {
        return pojazdRepository.findAll();
    }

    @PostMapping
    public String add(@RequestBody Pojazd pojazd) {
    	if(pojazd == null) {
    		throw new BadRequestException("Pojazd nie może być pusty");
    	}
        int pId = pojazdRepository.save(pojazd).getIdPojazdu();

        return "Dodano pojazd o ID: " + Integer.toString(pId);
    }

    @GetMapping("/{id}")
    public Pojazd getById(@PathVariable Integer id) {
    	return pojazdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pojazdu o ID " + id + " nie istnieje"));
    }


    @PutMapping("/{id}")
    public Pojazd update(@PathVariable Integer id, @RequestBody Pojazd updated) {
    	if(updated == null) {
    		throw new BadRequestException("Dane pojazdu nie mogą być puste");
    	}
    	Pojazd pojazd = pojazdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono pojazdu o ID " + id));
        pojazd.setTypPojazdu(updated.getTypPojazdu());
        pojazd.setPojemnosc(updated.getPojemnosc());
        pojazd.setMarka(updated.getMarka());
        pojazd.setModel(updated.getModel());
        pojazd.setNrRejestr(updated.getNrRejestr());
        return pojazdRepository.save(pojazd);
    }

    

    @GetMapping("/szukaj")
    public List<Pojazd> searchPojazdy(
            @RequestParam(required = false) String typ,
            @RequestParam(required = false) String marka,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String nrRejestr
    ) {
        if (typ != null) {
            List<Pojazd> list = pojazdRepository.findByTypPojazdu(typ);
            if (list.isEmpty()) {
                throw new ResourceNotFoundException("Nie znaleziono pojazdów o typie: " + typ);
            }
            return list;
        }
        if (marka != null) {
            List<Pojazd> list = pojazdRepository.findByMarka(marka);
            if (list.isEmpty()) {
                throw new ResourceNotFoundException("Nie znaleziono pojazdów marki: " + marka);
            }
            return list;
        }
        if (model != null) {
            List<Pojazd> list = pojazdRepository.findByModel(model);
            if (list.isEmpty()) {
                throw new ResourceNotFoundException("Nie znaleziono pojazdów o modelu: " + model);
            }
            return list;
        }
        if (nrRejestr != null) {
            Optional<Pojazd> pojazd = pojazdRepository.findByNrRejestr(nrRejestr);
            return pojazd.map(List::of)
                    .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono pojazdu o numerze rejestracyjnym: " + nrRejestr));
        }

        throw new BadRequestException("Przynajmniej jeden parametr (typ, marka, model, nrRejestr) musi być podany.");
    }


    @GetMapping("/pojemnosc/filter")
    public List<Pojazd> getByPojemnosc(
            @RequestParam(required = false) Double greaterThan,
            @RequestParam(required = false) Double lessThan,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max
    ) {
        if (min != null && max != null && min > max) {
            throw new IllegalArgumentException("Minimalna pojemność nie może być większa niż maksymalna.");
        }

        List<Pojazd> pojazdy = StreamSupport.stream(pojazdRepository.findAll().spliterator(), false)
                                           .collect(Collectors.toList());

        if (greaterThan != null) {
            pojazdy = pojazdy.stream()
                    .filter(p -> p.getPojemnosc() > greaterThan)
                    .collect(Collectors.toList());
        }

        if (lessThan != null) {
            pojazdy = pojazdy.stream()
                    .filter(p -> p.getPojemnosc() < lessThan)
                    .collect(Collectors.toList());
        }

        if (min != null && max != null) {
            pojazdy = pojazdy.stream()
                    .filter(p -> p.getPojemnosc() >= min && p.getPojemnosc() <= max)
                    .collect(Collectors.toList());
        }

        if (pojazdy.isEmpty()) {
            throw new ResourceNotFoundException("Nie znaleziono pojazdów spełniających podane kryteria pojemności.");
        }

        return pojazdy;
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePojazd(@PathVariable String id) {
        try {
            Integer pid = Integer.parseInt(id);
            Pojazd pojazd = pojazdRepository.findById(pid)
                    .orElseThrow(() -> new ResourceNotFoundException("Pojazd o ID " + pid + " nie istnieje."));

            // Odłącz wszystkie dostawy przypisane do pojazdu
            if (pojazd.getDostawy() != null) {
                for (Dostawa dostawa : pojazd.getDostawy()) {
                    dostawa.setPojazd(null);
                    dostawaRepository.save(dostawa); // zapisz zmiany
                }
            }

            pojazdRepository.delete(pojazd);
            return ResponseEntity.ok("Pojazd o ID " + pid + " został usunięty.");
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID musi być liczbą całkowitą: " + id);
        }
    }
    
    @DeleteMapping("/rejestr/{nr}")
    public void deleteByNrRejestr(@PathVariable String nr) {
        pojazdRepository.deleteByNrRejestr(nr);
    }
}
