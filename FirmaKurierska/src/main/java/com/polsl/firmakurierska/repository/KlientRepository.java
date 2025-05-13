package com.polsl.firmakurierska.repository;

import com.polsl.firmakurierska.model.Klient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KlientRepository extends CrudRepository<Klient, Integer> {

    List<Klient> findByImieK(String imieK);

    List<Klient> findByNazwiskoK(String nazwiskoK);

    List<Klient> findByImieKAndNazwiskoK(String imieK, String nazwiskoK);

    boolean existsByImieKAndNazwiskoK(String imieK, String nazwiskoK);

    List<Klient> findByImieKIgnoreCase(String imieK);

    List<Klient> findByNazwiskoKIgnoreCase(String nazwiskoK);

    List<Klient> findByImieKAndNazwiskoKIgnoreCase(String imieK, String nazwiskoK);


}
