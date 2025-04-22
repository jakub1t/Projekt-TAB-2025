package com.polsl.firmakurierska.repository;

import com.polsl.firmakurierska.model.Pojazd;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PojazdRepository extends CrudRepository<Pojazd, Integer> {

    List<Pojazd> findByTypPojazdu(String typPojazdu);

    List<Pojazd> findByMarka(String marka);

    List<Pojazd> findByModel(String model);

    Optional<Pojazd> findByNrRejestr(String nrRejestr);

    List<Pojazd> findByPojemnoscGreaterThan(double pojemnosc);

    List<Pojazd> findByPojemnoscLessThan(double pojemnosc);

    List<Pojazd> findByPojemnoscBetween(double pojemnoscStart, double pojemnoscEnd);

    boolean existsByNrRejestr(String nrRejestr);

    long countByMarka(String marka);

    long countByTypPojazdu(String typPojazdu);

    void deleteByNrRejestr(String nrRejestr);
}
