package com.polsl.firmakurierska.controller;

import com.polsl.firmakurierska.dto.PaczkaCreateDTO;
import com.polsl.firmakurierska.dto.PaczkaDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Paczka;
import com.polsl.firmakurierska.model.Produkt;
import com.polsl.firmakurierska.repository.DostawaRepository;
import com.polsl.firmakurierska.repository.KlientRepository;
import com.polsl.firmakurierska.repository.PaczkaRepository;
import com.polsl.firmakurierska.repository.ProduktRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/paczka")
public class PaczkaController {

    @Autowired
    PaczkaRepository paczkaRepository;
    
    @Autowired
    ProduktRepository produktRepository;

    @Autowired
    private KlientRepository klientRepository;

    @Autowired
    private DostawaRepository dostawaRepository;
    
    @GetMapping
    public List<PaczkaDTO> getAllPaczki() {
    	List<PaczkaDTO> paczki = StreamSupport.stream(paczkaRepository.findAll().spliterator(), false)
                .map(PaczkaDTO::new)
                .collect(Collectors.toList());
        if(paczki.isEmpty()) {
        	throw new ResourceNotFoundException("Brak paczek w bazie");
        }
        return paczki;
    }
 
    @GetMapping("/{id}")
    public PaczkaDTO getPaczkaById(@PathVariable String id) {
        try {
            Integer pid = Integer.parseInt(id);
            Paczka paczka = paczkaRepository.findById(pid)
            		.orElseThrow(() -> new ResourceNotFoundException("Paczka o ID \" + pid + \" nie znaleziona"));
            paczka.getProdukt().size();
            return new PaczkaDTO(paczka);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID " + id + " ma nieprawidłowy format. Oczekiwano liczby całkowitej.");
        }
    }
    
    
    @PostMapping("/savePaczka")
    public ResponseEntity<String> savePaczka(@RequestBody List<Paczka> paczkaData){
    	if(paczkaData == null ||paczkaData.isEmpty()) {
    		throw new BadRequestException("Lista paczek nie może być pusta.");
    	}
    	paczkaRepository.saveAll(paczkaData);
    	return ResponseEntity.ok("Data saved");
    }
    
    @PostMapping
    public ResponseEntity<PaczkaDTO> addPaczka(@Valid @RequestBody PaczkaCreateDTO dto) {
        Paczka paczka = new Paczka();
        paczka.setWagaPaczki(dto.getWagaPaczki());

        // Ustaw klienta jeśli podano
        if (dto.getKlientId() != null) {
            // musisz mieć klientRepository
            var klient = klientRepository.findById(dto.getKlientId())
                    .orElseThrow(() -> new BadRequestException("Klient o ID " + dto.getKlientId() + " nie istnieje"));
            paczka.setKlient(klient);
        }

        // Ustaw dostawę jeśli podano
        if (dto.getDostawaId() != null) {
            var dostawa = dostawaRepository.findById(dto.getDostawaId())
                    .orElseThrow(() -> new BadRequestException("Dostawa o ID " + dto.getDostawaId() + " nie istnieje"));
            paczka.setDostawa(dostawa);
        }

        // Produkty - pobierz po ID i ustaw do paczki
        if (dto.getProduktIds() != null && !dto.getProduktIds().isEmpty()) {
            var produkty = dto.getProduktIds().stream()
                    .map(id -> produktRepository.findById(id)
                            .orElseThrow(() -> new BadRequestException("Produkt o ID " + id + " nie istnieje")))
                    .collect(Collectors.toList());

            // ustaw listę produktów w paczce
            paczka.setProdukt(produkty);

            // ustaw paczkę w każdym produkcie (dwukierunkowo)
            produkty.forEach(p -> p.setPaczka(paczka));
        }

        // Zapisz paczkę
        Paczka saved = paczkaRepository.save(paczka);

        return ResponseEntity.status(HttpStatus.CREATED).body(new PaczkaDTO(saved));
    }
 
    @PutMapping("/{id}")
    public Paczka updatePaczka(@PathVariable Integer id, @RequestBody Paczka paczkaDetails) {
        Paczka paczka = paczkaRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException("Paczka o ID " + id + " nie istnieje"));
        if (paczkaDetails.getWagaPaczki() <= 0) {
            throw new BadRequestException("Waga paczki musi być większa niż 0.");
        }
        paczka.setWagaPaczki(paczkaDetails.getWagaPaczki());
        return paczkaRepository.save(paczka);
    }

    @DeleteMapping("/delete/{id}")
    public void deletePaczka(@PathVariable Integer id) {
        Paczka paczka = paczkaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paczka o ID " + id + " nie istnieje"));

        // Wyzeruj powiązania z produktami i zapisz je
        if (paczka.getProdukt() != null) {
            for (Produkt p : paczka.getProdukt()) {
                p.setPaczka(null);
                produktRepository.save(p); // <-- konieczne zapisanie zmian
            }
        }

        paczkaRepository.delete(paczka);
    }



    @GetMapping("/waga")
    public List<Paczka> filterByWaga(
            @RequestParam(required = false) Double equal,
            @RequestParam(required = false) Double greater,
            @RequestParam(required = false) Double less,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max
    ) {
        List<Paczka> paczki;

        if (equal != null) {
            paczki = paczkaRepository.findByWagaPaczki(equal);
        } else if (greater != null) {
            paczki = paczkaRepository.findByWagaPaczkiGreaterThan(greater);
        } else if (less != null) {
            paczki = paczkaRepository.findByWagaPaczkiLessThan(less);
        } else if (min != null && max != null) {
            paczki = paczkaRepository.findByWagaPaczkiBetween(min, max);
        } else {
            throw new BadRequestException("Musisz podać przynajmniej jeden parametr: equal, greater, less lub min i max.");
        }

        if (paczki.isEmpty()) {
            throw new ResourceNotFoundException("Brak paczek spełniających podane kryterium wagi.");
        }

        return paczki;
    }
}
