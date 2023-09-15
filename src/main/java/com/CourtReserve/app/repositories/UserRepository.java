package com.CourtReserve.app.repositories;

import com.CourtReserve.app.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    @Query("select u from User u where u.mobileNo = ?1 and u.password = ?2")
    User findByMobileNoAndPassword(String mobileNo, String password);
    // You can define additional methods for custom queries or operations

    User findByMobileNo(String MobileNo);

    List<User> findByOrderByIdDesc();
    User findByPassword( String Password);
    List <User> findByUserNameOrderByUserNameAsc(String userName);
    List<User> findByOrderByUserNameAsc();
    List <User> findByUserTypeOrderByUserNameAsc(String userType);
    List <User> findByCountryOrderByUserNameAsc(String country);
    List <User> findByReferralOrderByUserNameAsc(String referral);
    List <User> findByUserNameAndUserTypeOrderByUserNameAsc(String userName,String userType);
    List <User> findByCountryAndReferralOrderByUserNameAsc(String country,String referral);
    List <User> findByUserNameAndReferralOrderByUserNameAsc(String userName,String referral);
    List <User> findByCountryAndUserTypeOrderByUserNameAsc(String country,String userType);
    List <User> findByCountryAndUserTypeAndUserNameOrderByUserNameAsc(String country,String userType,String userName);
    List <User>  findByCountryAndUserNameAndReferralOrderByUserNameAsc(String country,String userName,String referral);
    List <User> findByUserNameAndUserTypeAndReferralOrderByUserNameAsc(String userName,String userType,String referral);
    List <User> findByCountryAndUserTypeAndReferralOrderByUserNameAsc(String country,String userType,String referral);
    List <User> findByCountryAndUserTypeAndReferralAndUserNameOrderByUserNameAsc(String country,String userType,String referral,String userName);
//    @Query("select distinct user_type from user")
//
//    List<User> findByOrderByDistinctUserType();


}
