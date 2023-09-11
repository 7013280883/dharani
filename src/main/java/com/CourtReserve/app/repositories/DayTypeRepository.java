package com.CourtReserve.app.repositories;

import com.CourtReserve.app.models.DayType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DayTypeRepository extends CrudRepository<DayType, Integer> {
    List<DayType> findByOrderByIdDesc();
    List<DayType> findByOrderByCodeAsc();
}