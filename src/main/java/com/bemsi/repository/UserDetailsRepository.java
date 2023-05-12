package com.bemsi.repository;

import com.bemsi.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

    @Query(value = "SELECT * FROM user_details u WHERE u.role & 2 = 2",
            nativeQuery = true)
    List<UserDetails> findAllDoctors();
}
