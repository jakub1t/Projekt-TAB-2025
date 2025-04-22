package com.polsl.firmakurierska.repository;

import com.polsl.firmakurierska.model.Producent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProducentRepository extends CrudRepository<Producent, Integer> {

    List<Producent> findByNazwaProducenta(String nazwaProducenta);

    long countByNazwaProducenta(String nazwaProducenta);

    boolean existsByNazwaProducenta(String nazwaProducenta);
}
