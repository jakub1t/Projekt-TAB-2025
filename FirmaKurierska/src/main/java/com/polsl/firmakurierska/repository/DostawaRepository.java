package com.polsl.firmakurierska.repository;

import com.polsl.firmakurierska.model.Dostawa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DostawaRepository extends CrudRepository<Dostawa, Integer> {

    List<Dostawa> findByPunktA(String punktA);

    List<Dostawa> findByPunktB(String punktB);

    List<Dostawa> findByDataWyruszenia(LocalDate dataWyruszenia);

    List<Dostawa> findByTermin(LocalDate termin);

    List<Dostawa> findByDataWyruszeniaBefore(LocalDate dataWyruszenia);

    List<Dostawa> findByTerminAfter(LocalDate termin);

    List<Dostawa> findByDataWyruszeniaBetween(LocalDate startDate, LocalDate endDate);

    List<Dostawa> findByPunktAAndPunktB(String punktA, String punktB);

    List<Dostawa> findAllByOrderByTerminAsc();

    List<Dostawa> findAllByOrderByTerminDesc();
}
