package com.polsl.firmakurierska.repository;

import com.polsl.firmakurierska.model.PrawoJazdy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrawoJazdyRepository extends CrudRepository<PrawoJazdy, Integer> {

    List<PrawoJazdy> findByKategoria(String kategoria);

    long countByKategoria(String kategoria);

    boolean existsByKategoria(String kategoria);
}
