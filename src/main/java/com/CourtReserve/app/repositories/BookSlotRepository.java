package com.CourtReserve.app.repositories;

import com.CourtReserve.app.models.BookSlot;
import com.azure.spring.data.cosmos.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookSlotRepository extends CrudRepository<BookSlot, Integer> {
    List<BookSlot> findByGameDateBetweenAndBookedByAndConfirmStatusAndGameModeOrderByIdAsc(LocalDate gameDateStart, LocalDate gameDateEnd, String bookedBy, String confirmStatus, String gameMode);
    List<BookSlot> findByGameDateBetweenAndConfirmStatus(LocalDate gameDateStart, LocalDate gameDateEnd, String confirmStatus);
    List<BookSlot> findByGameDateBetweenOrderByIdAsc(LocalDate gameDateStart, LocalDate gameDateEnd);
    List<BookSlot> findByGameDateAndGameModeAndSlotCode(LocalDate gameDate, String gameMode, String slotCode);
    List<BookSlot> findByGameDateAndConfirmStatus(LocalDate gameDate, String confirmStatus);
    List<BookSlot> findByBookedByOrderBySlotCodeAscGameDateAscCourtCodeAsc(String bookedBy);
    List<BookSlot> findByConfirmStatusAndGameDateAfterOrGameDateAndConfirmStatusOrderBySlotCodeAscGameDateAsc(String confirmStatus, LocalDate gameDate, LocalDate gameDate1, String confirmStatus1);
    List<BookSlot> findByConfirmStatusAndGameDateOrGameDateAfterAndConfirmStatusOrderByGameDateAsc(String confirmStatus, LocalDate gameDate, LocalDate gameDate1, String confirmStatus1);
    List<BookSlot> findByGameDateAfterOrGameDateAndBookedBy(LocalDate gameDate, LocalDate gameDate1, String bookedBy);
    BookSlot findByGameDateAndSlotCodeAndBookedBy(LocalDate gameDate, String slotCode, String bookedBy);
    List<BookSlot> findByGameDateBeforeAndBookedBy(LocalDate gameDate, String bookedBy);
    List<BookSlot> findByGameDateAfter(LocalDate gameDate);
    List<BookSlot> findByGameDateBefore(LocalDate gameDate);
    BookSlot findBySlotCodeAndGameDateAndConfirmStatus(String slotCode, LocalDate gameDate, String confirmStatus);
    List<BookSlot> findBySlotCodeAndGameDate(String slotCode, LocalDate gameDate);
//    @Query("select * from book_slot where booked_by=?1")
//
//    BookSlot findByBookedByAndStatus(String bookedBy,);

    List<BookSlot> findByBookedByAndGameDateOrGameDateAfterAndBookedBy(String mobileNo, LocalDate now, LocalDate now1, String mobileNo1);

    List<BookSlot> findByBookedByAndGameDateOrGameDateBeforeAndBookedBy(String mobileNo, LocalDate now, LocalDate now1, String mobileNo1);

    List<BookSlot> GameDateBeforeAndBookedBy(LocalDate now, String mobileNo);


    List<BookSlot> findByGameDateAndSlotCode(LocalDate date, String slotCode);

    List<BookSlot> findByBookedByOrderBySlotCodeDescGameDateDescCourtCodeDesc(String mobileNo);

    List<BookSlot> findByGameDateAndConfirmStatusAndSlotCode(LocalDate dateModified, String pending, String slotCode);

    List<BookSlot> findByGameDate(LocalDate dateModified);

    List<BookSlot> findByBookedByOrderBySlotCodeDescGameDateDesc(String mobileNo);

    List<BookSlot> findByGameDateAndConfirmStatusAndSlotCodeAndGameMode(LocalDate gameDate, String accepted, String slotCode,String gameMode);

    List<BookSlot> findByBookedByOrderByGameDateDesc(String mobileNo);

    List<BookSlot> findByBookedByAndGameDate(String mob, LocalDate date1);

    List<BookSlot> findByBookedBy(String loggedMobile);


    List findByGameDateBetweenAndBookedByAndGameModeOrderByIdAsc(LocalDate fromDate, LocalDate toDate, String mobileNo, String gameMode);

    List findByGameDateBetweenAndConfirmStatusAndGameModeOrderByIdAsc(LocalDate fromDate, LocalDate toDate, String status, String gameMode);

    List findByGameDateBetweenAndConfirmStatusAndBookedByOrderByIdAsc(LocalDate fromDate, LocalDate toDate, String status, String gameMode);
//    @Query("select * from book_slot where booked_by=?1,book_date=?2,game_date=?3.game_mode=?4,confirm_status")
//    List<BookSlot> findByBookedByAndGameDateBetweenAndGameModeAndConfirmStatus(String mobileNo,LocalDate bookDate, LocalDate gameDate,String gameMode,String confirmStatus);
    List findByGameDateBetweenAndBookedByOrderByIdAsc(LocalDate fromDate, LocalDate toDate, String bookedBy);
    List findByGameDateBetweenAndBookedByAndConfirmStatusOrderByIdAsc(LocalDate fromDate, LocalDate toDate, String bookedBy,String confirmSatus);

    List<BookSlot>findByGameDateBetween(LocalDate fromDate, LocalDate toDate);
    List findByGameDateBetweenAndConfirmStatusAndGameNameAndGameModeOrderByIdAsc(LocalDate fromDate, LocalDate toDate,String status,String name,String gameMode);
    List findByGameDateBetweenAndConfirmStatusOrderByIdAsc(LocalDate fromDate, LocalDate toDate,String status);
   // List findByGameDateBetweenAndGameModeOrderByIdAsc(LocalDate fromDate, LocalDate toDate,String gameMode);
    List findByGameDateBetweenAndGameNameAndGameModeOrderByIdAsc(LocalDate fromDate, LocalDate toDate,String name,String gameMode);
    List findByGameDateBetweenAndConfirmStatusAndGameNameOrderByIdAsc(LocalDate fromDate, LocalDate toDate,String status,String name);
    List findByGameDateBetweenAndBookedByOrderByGameDateAsc(LocalDate fromDate, LocalDate toDate,String mobileNo);
    List findByGameDateBetweenAndGameModeOrderByIdAsc(LocalDate fromDate, LocalDate toDate,String gameMode);
    List findByGameDateBetweenAndBookedByAndGameModeAndConfirmStatusOrderByIdAsc(LocalDate fromDate, LocalDate toDate,String mobileNo,String gameMode,String status);
    List<BookSlot> findByBookedByAndGameDateOrGameDateAfterAndBookedByOrderByGameDateAscCourtCodeAscStartTimeAsc(String mobileNo, LocalDate now, LocalDate now1, String mobileNo1);
}