package com.CourtReserve.app.repositories;

import com.CourtReserve.app.models.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Game, Integer> {
    List<Game> findByOrderByIdDesc();
    List<Game> findByName(String name);
    List<Game> findByCode(String code);
    List<Game> findByNameAndCode(String name,String code);
    List<Game> findByOrderByNameAsc();
}