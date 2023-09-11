package com.CourtReserve.app.repositories;

import com.CourtReserve.app.models.SpecialDates;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpecialDatesRepository extends CrudRepository<SpecialDates, Integer> {
    SpecialDates findByDate(String date);
    List<SpecialDates> findByOrderByIdDesc();
    List<SpecialDates> findByCourtCodeOrderByCourtCodeAsc(String courtCode);
    List<SpecialDates> findByDayTypeOrderByCourtCodeAsc(String dayType);
    List<SpecialDates> findByCourtCodeAndDayTypeOrderByCourtCodeAsc(String courtCode,String dayType);
    List<SpecialDates> findByOrderByCourtCodeAsc();
}