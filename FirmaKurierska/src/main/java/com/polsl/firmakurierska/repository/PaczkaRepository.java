package com.polsl.firmakurierska.repository;

import com.polsl.firmakurierska.model.Paczka;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaczkaRepository extends CrudRepository<Paczka, Integer> {

    List<Paczka> findByWagaPaczki(double wagaPaczki);

    List<Paczka> findByWagaPaczkiGreaterThan(double wagaPaczki);

    List<Paczka> findByWagaPaczkiLessThan(double wagaPaczki);

    List<Paczka> findByWagaPaczkiBetween(double wagaStart, double wagaEnd);

    long countByWagaPaczki(double wagaPaczki);

    boolean existsByWagaPaczki(double wagaPaczki);

 // Metoda do znalezienia paczek dla danej dostawy
    List<Paczka> findByDostawa_IdDostawy(Integer id);
}
