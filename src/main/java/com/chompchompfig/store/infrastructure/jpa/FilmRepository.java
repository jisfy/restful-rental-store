package com.chompchompfig.store.infrastructure.jpa;

import com.chompchompfig.store.domain.Film;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A Repository of Films, as per DDD
 */
@Repository
public interface FilmRepository extends CrudRepository<Film, Long> {

    List<Film> findAll();

}
