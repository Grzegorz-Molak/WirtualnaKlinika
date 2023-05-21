package com.bemsi.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
    public interface InvalidatedTokensRepository extends JpaRepository<InvalidatedToken,Long> {
        public Optional<InvalidatedToken> findByToken(String token);
    }