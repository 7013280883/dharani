package com.CourtReserve.app.repositories;

import com.CourtReserve.app.models.Slot;
import com.azure.spring.data.cosmos.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SlotRepository extends CrudRepository<Slot, Integer> {
    //Slot findBySlotCode(String slotCode);
    List<Slot> findByDayTypeAndCourtCode(String dayType, String courtCode);
    List<Slot> findByOrderBySlotCodeAsc();
    List<Slot> findByDayTypeOrderBySlotCodeAsc(String dayType);

    List<Slot> findByDayTypeOrderByStartHourAsc(String dayType);
    @Query("select * from slot where court_code=?1 ")
    Slot findBySlotCode(String courtCode);

    List <Slot> findByOrderByIdDesc();
    List <Slot> findByCourtCode(String courtCode);
    List <Slot> findByCourtCodeAndDayType(String courtCode,String dayType);
}