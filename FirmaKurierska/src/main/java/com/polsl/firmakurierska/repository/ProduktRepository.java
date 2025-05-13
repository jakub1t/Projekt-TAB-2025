package com.polsl.firmakurierska.repository;

import com.polsl.firmakurierska.model.Produkt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduktRepository extends CrudRepository<Produkt, Integer> {

    Optional<Produkt> findByNrSeryjny(String nrSeryjny);

    List<Produkt> findByKategoriaProd(String kategoriaProd);

    List<Produkt> findByNazwaProduktu(String nazwaProduktu);

    List<Produkt> findByWagaGreaterThan(double waga);

    List<Produkt> findByWagaLessThan(double waga);

    List<Produkt> findByWagaBetween(double wagaStart, double wagaEnd);

    boolean existsByNrSeryjny(String nrSeryjny);

    long countByKategoriaProd(String kategoriaProd);

    long countByNazwaProduktu(String nazwaProduktu);

    void deleteByNrSeryjny(String nrSeryjny);
    
    boolean existsByProducent_IdProducenta(Integer idProducenta);
}
