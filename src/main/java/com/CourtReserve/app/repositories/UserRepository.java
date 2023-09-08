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
    List <User> findByUserName(String userName);
    List <User> findByUserType(String userType);
    List <User> findByCountry(String country);
    List <User> findByReferral(String referral);
    List <User> findByUserNameAndUserType(String userName,String userType);
    List <User> findByCountryAndReferral(String country,String referral);
    List <User> findByUserNameAndReferral(String userName,String referral);
    List <User> findByCountryAndUserType(String country,String userType);
    List <User> findByCountryAndUserTypeAndUserName(String country,String userType,String userName);
    List <User>  findByCountryAndUserNameAndReferral(String country,String userName,String referral);
    List <User> findByUserNameAndUserTypeAndReferral(String userName,String userType,String referral);
    List <User> findByCountryAndUserTypeAndReferral(String country,String userType,String referral);
    List <User> findByCountryAndUserTypeAndReferralAndUserName(String country,String userType,String referral,String userName);

}
