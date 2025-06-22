package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.dto.PracownikDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Dostawa;
import com.polsl.firmakurierska.model.Konto;
import com.polsl.firmakurierska.model.Pracownik;
import com.polsl.firmakurierska.model.PrawoJazdy;
import com.polsl.firmakurierska.repository.PracownikRepository;
import com.polsl.firmakurierska.repository.KontoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/pracownik")
public class PracownikController {

    @Autowired
    PracownikRepository pracownikRepository;

    @Autowired
    KontoRepository kontoRepository;
    @GetMapping
    public @ResponseBody Iterable<PracownikDTO> getAllPracownicy() {
    	Iterable<Pracownik> pracownicy = pracownikRepository.findAll();
    	if(!pracownicy.iterator().hasNext()) {
    		throw new ResourceNotFoundException("Brak pracowników w bazie");
    	}
        return StreamSupport.stream(pracownikRepository.findAll().spliterator(), false)
                .map(PracownikDTO::new)
                .collect(Collectors.toList());
    }
 
    @PostMapping("/{id}")
    public ResponseEntity<PracownikDTO> getPracownikById(@PathVariable String id) {
        int pid;
        try {
            pid = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID musi być liczbą całkowitą: " + id);
        }

        Pracownik pracownik = pracownikRepository.findById(pid)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono pracownika o ID: " + pid));

        return ResponseEntity.ok(new PracownikDTO(pracownik));
    }

    @GetMapping("/get")
    public Pracownik getPracownikByIdRaw(@RequestParam String id) {
        int pid;
        try {
            pid = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID musi być liczbą całkowitą: " + id);
        }
        Pracownik pracownik = pracownikRepository.findById(pid)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono pracownika o ID: " + pid));

        return pracownik;
    }
    
   
    @PostMapping
    public Pracownik addPracownik(@RequestBody Pracownik pracownik) {
        if(pracownik == null) {
            throw new BadRequestException("Dane pracownika nie mogą być puste.");
        }

        // Sprawdź czy konto ma ustawione id i istnieje
        if (pracownik.getKonto() != null && pracownik.getKonto().getIdKonta() != null) {
            Integer kontoId = pracownik.getKonto().getIdKonta();
            Konto konto = kontoRepository.findById(kontoId)
                    .orElseThrow(() -> new BadRequestException("Konto o ID " + kontoId + " nie istnieje"));
            pracownik.setKonto(konto); // przypisz załadowane konto
        } else {
            throw new BadRequestException("Pracownik musi mieć przypisane istniejące konto (idKonta)");
        }

        return pracownikRepository.save(pracownik);
    }


    @Transactional
    @DeleteMapping("/{pesel}")
    public void deletePracownikByPesel(@PathVariable String pesel) {
        Pracownik pracownik = pracownikRepository.findByPesel(pesel)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono pracownika o PESEL: " + pesel));
        pracownikRepository.delete(pracownik);
    }


    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<String> deletePracownik(@PathVariable Integer id) {
        Optional<Pracownik> optional = pracownikRepository.findById(id);
        if (optional.isPresent()) {
            Pracownik pracownik = optional.get();

            // Usuń referencje z drugiej strony relacji
            if (pracownik.getPrawoJazdy() != null) {
                for (PrawoJazdy pj : pracownik.getPrawoJazdy()) {
                    pj.getPracownicy().remove(pracownik);
                }
                pracownik.getPrawoJazdy().clear();
            }

            // Usuń referencje z dostaw
            if (pracownik.getDostawy() != null) {
                for (Dostawa d : pracownik.getDostawy()) {
                    d.setPracownik(null);  // o ile Dostawa ma `@ManyToOne Pracownik`
                }
            }

            pracownik.setStanowisko(null);
            pracownik.setKonto(null); // Konto ma CascadeType.ALL, więc zostanie usunięte

            pracownikRepository.save(pracownik); // zapis zmian w relacjach
            pracownikRepository.delete(pracownik); // teraz można bezpiecznie usunąć

            return ResponseEntity.ok("Pracownik został usunięty.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pracownik nie istnieje.");
        }
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
    
    @PutMapping("/{pesel}")
    public Pracownik updatePracownik(@PathVariable String pesel, @RequestBody Pracownik pracownikDetails) {
        Pracownik pracownik = pracownikRepository.findByPesel(pesel)
                .orElseThrow(() -> new RuntimeException("Pracownik z pesel " + pesel + " nie istnieje"));

        
        pracownik.setImie(pracownikDetails.getImie());
        pracownik.setNazwisko(pracownikDetails.getNazwisko());
        pracownik.setStanowisko(pracownikDetails.getStanowisko());

        return pracownikRepository.save(pracownik);  
    }


///////////////Pytania z parametrami
	
    @GetMapping("/szukaj")
    public ResponseEntity<CollectionModel<EntityModel<PracownikDTO>>> searchPracownicy(
            @RequestParam(required = false) String imie,
            @RequestParam(required = false) String nazwisko,
            @RequestParam(required = false) String pesel,
            @RequestParam(required = false) String imieStartsWith
    ) {
        List<Pracownik> pracownicy;

        if (imie != null && nazwisko != null) {
            pracownicy = pracownikRepository.findByImieAndNazwisko(imie, nazwisko);
        } else if (imie != null) {
            pracownicy = pracownikRepository.findByImie(imie);
        } else if (nazwisko != null) {
            pracownicy = pracownikRepository.findByNazwisko(nazwisko);
        } else if (pesel != null) {
            Optional<Pracownik> p = pracownikRepository.findByPesel(pesel);
            pracownicy = p.map(List::of).orElse(List.of());
        } else if (imieStartsWith != null) {
            pracownicy = pracownikRepository.findByImieStartingWith(imieStartsWith);
        } else {
            throw new BadRequestException("Brak parametrów zapytania. Podaj imię, nazwisko, pesel lub imieStartsWith.");
        }

        if (pracownicy.isEmpty()) {
            throw new ResourceNotFoundException("Nie znaleziono pracowników dla podanych parametrów.");
        }

        List<EntityModel<PracownikDTO>> dtoList = pracownicy.stream()
                .map(PracownikDTO::new)
                .map(dto -> EntityModel.of(dto,
                        linkTo(methodOn(PracownikController.class).getPracownikById(dto.getIdOsoby().toString())).withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(dtoList,
                linkTo(methodOn(PracownikController.class).searchPracownicy(imie, nazwisko, pesel, imieStartsWith)).withSelfRel()));
    }
}