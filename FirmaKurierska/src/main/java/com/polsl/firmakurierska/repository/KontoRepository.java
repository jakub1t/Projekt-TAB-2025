package com.polsl.firmakurierska.repository;

import com.polsl.firmakurierska.model.Konto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KontoRepository extends CrudRepository<Konto, Integer> {

    Optional<Konto> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByHaslo(String haslo);

    Optional<Konto> findByLoginAndHaslo(String login, String haslo);

    void deleteByLogin(String login);

    long countByLogin(String login);
}
