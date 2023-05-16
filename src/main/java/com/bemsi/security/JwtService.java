package com.bemsi.security;

import com.bemsi.model.User;
import com.bemsi.model.UserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;


@Service
public class JwtService {

    //SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    //String secretString = Encoders.BASE64.encode(secret.getEncoded());

    SecretKey key;
    public JwtService(@Value("${jwt.secret.key}") String secretString){
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
    }

    public String generateJws(String login) {
        final int MINUTES = 5;
        return Jwts.builder()
                .setSubject(login)
                .setExpiration(DateUtils.addMinutes(new Date(), MINUTES))
                .signWith(key)
                .compact();
    }

    public boolean validateJws(String jwsString){
        Jws<Claims> jws;
        try {
            jws = Jwts.parserBuilder()  // (1)
                    .setSigningKey(key)         // (2)
                    .build()                    // (3)
                    .parseClaimsJws(jwsString); // (4)
            if(new Date().after(jws.getBody().getExpiration()))
                return false;
        }
            // we can safely trust the JWT
        catch (JwtException ex) {       // (5)
                return false;
                // we *cannot* use the JWT as intended by its creator
            }
        return true;
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
