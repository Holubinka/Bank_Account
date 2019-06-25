package com.bank.atm.configuration.security;

import com.bank.atm.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

public class JWTAuthorizationFilter extends UsernamePasswordAuthenticationFilter {


    private AuthenticationManager authenticationManager;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Customer user = getUserFromInputStream(request);
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getcardNumber(),
                user.getPassword(),
                Collections.emptyList()
        ));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String token = Jwts.builder()
                .setSubject(((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername())
                .setExpiration(Date.from(LocalDate.now().plusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, "SALT".getBytes())
                .compact();

        response.addHeader("Authorization", token);
        response.addHeader("Access-Control-Expose-Headers", "Authorization");
    }

    private Customer getUserFromInputStream(HttpServletRequest request) {
        Customer user = null;

        try {
            user = new ObjectMapper().readValue(request.getInputStream(), Customer.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }
}
