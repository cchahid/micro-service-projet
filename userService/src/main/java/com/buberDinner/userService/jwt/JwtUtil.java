package com.buberDinner.userService.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    @SuppressWarnings("deprecation")
    private String generateToken(Map<String,Object> extraClaims, UserDetails details){
        // Utiliser une collection mutable pour les claims supplémentaires
        Map<String, Object> claims = new HashMap<>(extraClaims);
        return Jwts.builder().setClaims(claims).setSubject(details.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))
        .signWith(SignatureAlgorithm.HS256, getSingingKey()).compact();
    }
    @SuppressWarnings("deprecation")
    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(getSingingKey()).parseClaimsJws(token).getBody();
    } 
    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public String generateToken(UserDetails userDetalis){
        return generateToken(new HashMap<>(), userDetalis);
    }
    public Boolean validateToken(String token, UserDetails details){
        final String username=extractUsername(token);
        return (username.equals(details.getUsername()) && !isTokenExpired(token));
    }

    private Key getSingingKey(){
        byte[] keyBytes=Decoders.BASE64.decode("rtwRw9zI4IXy+wP3e5rxffISsE+THeMRaz/i+zaQVFM=");
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
