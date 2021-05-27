package com.geoit.mrm.users.api.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;

@Service
public class JwtTokenService {
    @Value("${jwt.login.secret}")
    private String jwtLoginSecret;

    public Map<String, Object> getJwtLoginTokenClaims(String token) {
        return Jwts.parser().setSigningKey(jwtLoginSecret).parseClaimsJws(token).getBody();
    }
}