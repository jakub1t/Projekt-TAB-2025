package com.polsl.firmakurierska.repository;

import com.polsl.firmakurierska.model.Stanowisko;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StanowiskoRepository extends CrudRepository<Stanowisko, Integer> {

    List<Stanowisko> findByNazwaStanowiska(String nazwaStanowiska);

    long countByNazwaStanowiska(String nazwaStanowiska);

    boolean existsByNazwaStanowiska(String nazwaStanowiska);
}
