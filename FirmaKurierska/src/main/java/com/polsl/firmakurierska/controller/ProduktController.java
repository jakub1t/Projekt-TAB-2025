package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.dto.ProduktDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Produkt;
import com.polsl.firmakurierska.repository.ProduktRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/produkt")
public class ProduktController {

    @Autowired
    private ProduktRepository produktRepository;

    @GetMapping
    public @ResponseBody Iterable<ProduktDTO> getAllProdukty() {
        Iterable<Produkt> produkty = produktRepository.findAll();
        if (!produkty.iterator().hasNext()) {
            throw new ResourceNotFoundException("Brak produktów w bazie.");
        }
        return StreamSupport.stream(produkty.spliterator(), false)
                .map(ProduktDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<Object> getProduktById(@PathVariable String id) {
        try {
            Integer pid = Integer.parseInt(id);
            Produkt produkt = produktRepository.findById(pid).orElse(null);
            if (produkt == null) {
                throw new ResourceNotFoundException("Nie znaleziono produktu o ID: " + pid);
            } else {
                return ResponseEntity.ok(new ProduktDTO(produkt));
            }
        } catch (NumberFormatException e) {
            throw new BadRequestException("Błędny format ID: '" + id + "'. Musi być liczbą całkowitą.");
        }
    }

    @PostMapping
    public ResponseEntity<Object> addProdukt(@RequestBody Produkt produkt) {
        if (produkt.getNazwaProduktu() == null || produkt.getNazwaProduktu().isEmpty()) {
            throw new BadRequestException("Nazwa produktu jest wymagana.");
        }
        if(produkt.getKategoriaProd() == null || produkt.getKategoriaProd().isEmpty()) {
        	throw new BadRequestException("Kategoria produktu jest wymagana.");
        }
        if(produkt.getNrSeryjny() == null || produkt.getNrSeryjny().isEmpty()) {
        	throw new BadRequestException("NrSeryjny produktu jest wymagana.");
        }
        Produkt savedProdukt = produktRepository.save(produkt);
        return new ResponseEntity<>(new ProduktDTO(savedProdukt), HttpStatus.CREATED);
    }

    @DeleteMapping("/nrSeryjny")
    public ResponseEntity<Object> deleteProdukt(@RequestParam String nrSeryjny) {
        if (!produktRepository.existsByNrSeryjny(nrSeryjny)) {
            throw new ResourceNotFoundException("Produkt o numerze seryjnym: " + nrSeryjny + " nie istnieje.");
        }
        produktRepository.deleteByNrSeryjny(nrSeryjny);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Produkt o numerze seryjnym " + nrSeryjny + " został usunięty.");
    }

    @GetMapping("/filter")
    public List<ProduktDTO> getFilteredProdukty(
            @RequestParam(required = false) String kategoriaProd,
            @RequestParam(required = false) String nazwaProduktu,
            @RequestParam(required = false) Double wagaGreaterThan,
            @RequestParam(required = false) Double wagaLessThan,
            @RequestParam(required = false) Double wagaStart,
            @RequestParam(required = false) Double wagaEnd
    ) {
        if (kategoriaProd == null && nazwaProduktu == null && wagaGreaterThan == null &&
                wagaLessThan == null && wagaStart == null && wagaEnd == null) {
            throw new BadRequestException("Musisz podać przynajmniej jeden filtr (kategoria, nazwa, waga).");
        }

        List<Produkt> result = new ArrayList<>();

        if (wagaGreaterThan != null) {
            result = produktRepository.findByWagaGreaterThan(wagaGreaterThan);
            if (result.isEmpty()) {
                throw new ResourceNotFoundException("Nie znaleziono produktów o wadze większej niż: " + wagaGreaterThan);
            }
        } else if (kategoriaProd != null && !kategoriaProd.isEmpty()) {
            result = produktRepository.findByKategoriaProd(kategoriaProd);
            if (result.isEmpty()) {
                throw new ResourceNotFoundException("Nie znaleziono produktów w kategorii: " + kategoriaProd);
            }
        } else if (nazwaProduktu != null && !nazwaProduktu.isEmpty()) {
            result = produktRepository.findByNazwaProduktu(nazwaProduktu);
            if (result.isEmpty()) {
                throw new ResourceNotFoundException("Nie znaleziono produktów o nazwie: " + nazwaProduktu);
            }
        }

        // Jeśli są inne filtry
        if (wagaLessThan != null) {
            result = produktRepository.findByWagaLessThan(wagaLessThan);
            if (result.isEmpty()) {
                throw new ResourceNotFoundException("Nie znaleziono produktów o wadze mniejszej niż: " + wagaLessThan);
            }
        }

        if (wagaStart != null && wagaEnd != null) {
            result = produktRepository.findByWagaBetween(wagaStart, wagaEnd);
            if (result.isEmpty()) {
                throw new ResourceNotFoundException("Nie znaleziono produktów o wadze pomiędzy: " + wagaStart + " a " + wagaEnd);
            }
        }

        // Mapowanie na DTO
        return result.stream()
                .map(ProduktDTO::new) // mapujemy encje na DTO
                .collect(Collectors.toList());
    }

    @GetMapping("/count/kategoria/{kategoriaProd}")
    public long countByKategoriaProd(@PathVariable String kategoriaProd) {
        long count = produktRepository.countByKategoriaProd(kategoriaProd);
        if (count == 0) {
            throw new ResourceNotFoundException("Brak produktów w kategorii: " + kategoriaProd);
        }
        return count;
    }

    @GetMapping("/count/nazwa/{nazwaProduktu}")
    public long countByNazwaProduktu(@PathVariable String nazwaProduktu) {
        long count = produktRepository.countByNazwaProduktu(nazwaProduktu);
        if (count == 0) {
            throw new ResourceNotFoundException("Brak produktów o nazwie: " + nazwaProduktu);
        }
        return count;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProdukt(@PathVariable Integer id, @RequestBody Produkt produkt) {
        if (!produktRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produkt o ID " + id + " nie istnieje.");
        }
        produkt.setIdProduktu(id); 
        Produkt updatedProdukt = produktRepository.save(produkt);
        return ResponseEntity.ok(new ProduktDTO(updatedProdukt));
    }
}
