package com.bemsi.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Repository
    public interface InvalidatedTokensRepository extends JpaRepository<InvalidatedToken,Long> {
        Optional<InvalidatedToken> findByToken(String token);
        void removeAllByExpirationTimeBefore(Date date);
    }