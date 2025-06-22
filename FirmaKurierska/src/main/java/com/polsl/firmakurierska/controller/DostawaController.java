package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.dto.DostawaDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Dostawa;
import com.polsl.firmakurierska.model.Paczka;
import com.polsl.firmakurierska.repository.DostawaRepository;
import com.polsl.firmakurierska.repository.PaczkaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/dostawa")
public class DostawaController {

    @Autowired
    DostawaRepository dostawaRepository;
    
    @Autowired
    PaczkaRepository  paczkaRepository;

    @GetMapping
    public @ResponseBody Iterable<DostawaDTO> getAllDostawy() {
        return StreamSupport.stream(dostawaRepository.findAll().spliterator(), false)
                .map(DostawaDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DostawaDTO> getDostawaById(@PathVariable String id) {
        try {
            Integer did = Integer.parseInt(id);
            Dostawa dostawa = dostawaRepository.findById(did)
                    .orElseThrow(() -> new ResourceNotFoundException("Dostawa o ID " + did + " nie istnieje."));
            return ResponseEntity.ok(new DostawaDTO(dostawa));
        } catch (NumberFormatException e) {
            throw new BadRequestException("Nieprawidłowy format ID: '" + id + "'. ID musi być liczbą całkowitą.");
        }
    }


    @GetMapping("/pracownik/{id}")
    public @ResponseBody Iterable<DostawaDTO> getDostawaByPracownikId(@PathVariable String id) {
        try {
            Integer did = Integer.parseInt(id);
            return StreamSupport.stream(dostawaRepository.findByPracownikIdOsoby(did).spliterator(), false)
                .map(DostawaDTO::new)
                .collect(Collectors.toList());
        }
        catch (NumberFormatException e) {
            throw new BadRequestException("Nieprawidłowy format ID: '" + id + "'. ID musi być liczbą całkowitą.");
        }
    }


    @PostMapping("/add")
    public ResponseEntity<Object> addDostawa(@RequestBody DostawaDTO dto) {
        try {
            // Convert the Iterable to a List and then check if the delivery already exists
            List<Dostawa> existingDostawy = new ArrayList<>();
            dostawaRepository.findAll().forEach(existingDostawy::add);

            boolean exists = existingDostawy.stream().anyMatch(d ->
                    dto.getPunktA().equals(d.getPunktA()) &&
                    dto.getPunktB().equals(d.getPunktB()) &&
                    dto.getDataWyruszenia().equals(d.getDataWyruszenia()) &&
                    dto.getTermin().equals(d.getTermin())
            );

            if (exists) {
                // If the delivery already exists, return a BadRequestException
                throw new BadRequestException("Taka dostawa już istnieje.");
            }

            // If no duplicate found, create the new delivery
            Dostawa dostawa = new Dostawa();
            dostawa.setPunktA(dto.getPunktA());
            dostawa.setPunktB(dto.getPunktB());
            dostawa.setDataWyruszenia(dto.getDataWyruszenia());
            dostawa.setTermin(dto.getTermin());
            dostawa.setStatus(dto.getStatus()); // Add status here

            // Save the new delivery to the repository
            Dostawa savedDostawa = dostawaRepository.save(dostawa);
            return ResponseEntity.status(HttpStatus.CREATED).body(new DostawaDTO(savedDostawa));

        } catch (BadRequestException e) {
            throw e; // If a duplicate is found, throw a BadRequestException
        } catch (Exception e) {
            // If there's another error, throw a BadRequestException
            throw new BadRequestException("Nie udało się utworzyć dostawy: " + e.getMessage());
        }
    }


    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<String> deleteDostawa(@PathVariable String id) {
        try {
            Integer did = Integer.parseInt(id);
            Dostawa dostawa = dostawaRepository.findById(did)
                    .orElseThrow(() -> new ResourceNotFoundException("Dostawa o ID " + did + " nie istnieje."));

            // Odłącz paczki od tej dostawy
            if (dostawa.getPaczki() != null) {
                for (Paczka paczka : dostawa.getPaczki()) {
                    paczka.setDostawa(null);
                    // Zapisz zmiany paczki, żeby odłączyć dostawę
                    paczkaRepository.save(paczka);
                }
            }

            // Usuń dostawę
            dostawaRepository.delete(dostawa);
            return ResponseEntity.ok("Dostawa o ID " + did + " została usunięta.");
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID musi być liczbą całkowitą: " + id);
        }
    }



    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateDostawa(@PathVariable String id, @RequestBody DostawaDTO dto) {
        try {
            Integer did = Integer.parseInt(id);
            Dostawa dostawa = dostawaRepository.findById(did)
            		.orElseThrow(() -> new ResourceNotFoundException("Dostawa o ID " + did + " nie istnieje."));	

            if (dto.getPunktA() != null) {
                dostawa.setPunktA(dto.getPunktA());
            }
            if (dto.getPunktB() != null) {
                dostawa.setPunktB(dto.getPunktB());
            }
            if (dto.getDataWyruszenia() != null) {
                dostawa.setDataWyruszenia(dto.getDataWyruszenia());
            }
            if (dto.getTermin() != null) {
                dostawa.setTermin(dto.getTermin());
            }
            if (dto.getStatus() != null) {
                dostawa.setStatus(dto.getStatus());
            }

            dostawaRepository.save(dostawa);
            return ResponseEntity.ok("Dostawa o ID " + did + " została zaktualizowana.");
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID musi być liczbą całkowitą: " + id);
        }
    }

    // -----------------------------------
    // Wyszukiwanie według różnych kryteriów
    // -----------------------------------
    private LocalDate parseDate(String input, String fieldName) {
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Nieprawidłowy format daty w polu: " + fieldName + ". Oczekiwany format: yyyy-MM-dd.");
        }
    }

    
    @GetMapping("/filter")
    public ResponseEntity<List<DostawaDTO>> filterDostawy(
            @RequestParam(required = false) String punktA,
            @RequestParam(required = false) String punktB,
            @RequestParam(required = false) String dataWyruszenia,
            @RequestParam(required = false) String termin,
            @RequestParam(required = false) String sort) {

        try {
        	List<Dostawa> wynik = new ArrayList<>();
        	dostawaRepository.findAll().forEach(wynik::add);
        	
            // Filtrowanie punktA
            if (punktA != null) {
                wynik = wynik.stream()
                        .filter(d -> punktA.equals(d.getPunktA()))
                        .collect(Collectors.toList());
            }

            // Filtrowanie punktB
            if (punktB != null) {
                wynik = wynik.stream()
                        .filter(d -> punktB.equals(d.getPunktB()))
                        .collect(Collectors.toList());
            }

            // Filtrowanie po dataWyruszenia
            if (dataWyruszenia != null) {
                LocalDate date = parseDate(dataWyruszenia, "dataWyruszenia");
                wynik = wynik.stream()
                        .filter(d -> date.equals(d.getDataWyruszenia()))
                        .collect(Collectors.toList());
            }

            // Filtrowanie po termin
            if (termin != null) {
                LocalDate date = parseDate(termin, "termin");
                wynik = wynik.stream()
                        .filter(d -> date.equals(d.getTermin()))
                        .collect(Collectors.toList());
            }

            //Sortowanie
            if ("asc".equalsIgnoreCase(sort)) {
                wynik.sort(Comparator.comparing(Dostawa::getTermin));
            } else if ("desc".equalsIgnoreCase(sort)) {
                wynik.sort(Comparator.comparing(Dostawa::getTermin).reversed());
            }

            if (wynik.isEmpty()) {
                throw new ResourceNotFoundException("Brak dostaw spełniających podane kryteria.");
            }

            List<DostawaDTO> dtoList = wynik.stream().map(DostawaDTO::new).collect(Collectors.toList());
            return ResponseEntity.ok(dtoList);

        } catch (BadRequestException | ResourceNotFoundException ex) {
            throw ex; // obsłużone w GlobalExceptionHandler
        } catch (Exception e) {
            throw new BadRequestException("Błąd filtrowania: " + e.getMessage());
        }
    }
}
