package com.bank.atm.configuration.security;

import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {


    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        if (header == null) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken auth = getAuth(request);
        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuth(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null) {
            String username = Jwts.parser()
                    .setSigningKey("SALT".getBytes())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            if (username != null) {
                return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            }

            return null;
        }

        return null;
    }
}
