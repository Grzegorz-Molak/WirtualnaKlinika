package com.bemsi.security;

import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.model.Specialization;
import com.bemsi.model.User;
import com.bemsi.model.UserDetails;
import com.bemsi.repository.UserDetailsRepository;
import com.bemsi.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;


@Service
public class JwtService {


    //SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    //String secretString = Encoders.BASE64.encode(secret.getEncoded());

    SecretKey key;
    InvalidatedTokensRepository invalidatedTokensRepository;
    UserDetailsRepository userDetailsRepository;
    @Autowired
    public JwtService(@Value("${jwt.secret.key}") String secretString,
                      InvalidatedTokensRepository invalidatedTokensRepository,
                      UserDetailsRepository userDetailsRepository){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
        this.invalidatedTokensRepository = invalidatedTokensRepository;
        this.userDetailsRepository = userDetailsRepository;
    }

    public UserDetails authorization(@NotNull HttpServletRequest request){
            final String header = request.getHeader("Authorization");
            String jwt;
            if (header == null || !header.startsWith("Bearer ")){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
            }
            jwt = header.substring(7);
            var username = validateJws(jwt);
            if ( username.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
            }
            var user = userDetailsRepository.findById(Long.parseLong(username.get()));
            if(user.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
            }
            return user.get();
    }

    public String generateJws(String login) {
        final int MINUTES = 10;
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(login)
                .setExpiration(DateUtils.addMinutes(new Date(), MINUTES))
                .signWith(key)
                .compact();
    }

    public Optional<String> validateJws(String jwt){
        Jws<Claims> jws;
        try {
            jws = Jwts.parserBuilder()  // (1)
                    .setSigningKey(key)         // (2)
                    .build()                    // (3)
                    .parseClaimsJws(jwt); // (4)
            if(new Date().after(jws.getBody().getExpiration()))
                return Optional.empty();
        }
            // we can safely trust the JWT
        catch (JwtException ex) {       // (5)
                return Optional.empty();
                // we *cannot* use the JWT as intended by its creator
            }

        boolean isInvalidated = invalidatedTokensRepository.findByToken(jwt).isPresent();
        if(isInvalidated){
            return Optional.empty();
        }

        return Optional.of(extractUsername(jwt));
    }

    public void invalidateToken(String jwt){
        InvalidatedToken token = new InvalidatedToken(jwt, extractAllClaims(jwt).getExpiration());
        invalidatedTokensRepository.save(token);
    }

    public String extractUsername(String jwt){
        String username = extractAllClaims(jwt).getSubject();
        return username;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
