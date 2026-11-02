
package com.softpro.GavaiSwad.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {
    	

    	System.out.println("LOGIN USER: " + authentication.getName());
    	System.out.println("LOGIN AUTHORITIES: " + authentication.getAuthorities());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isDelivery = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DELIVERY"));

        
        if (isAdmin) {
            response.sendRedirect("/admin/dashboard");
        } else if (isDelivery) {
            response.sendRedirect("/delivery/dashboard");
        } else {
            response.sendRedirect("/");
        }
    }
}
