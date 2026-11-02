
package com.softpro.GavaiSwad.Controller;

import com.softpro.GavaiSwad.Entity.User;
import com.softpro.GavaiSwad.Repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelController {

    private final UserRepository userRepository;

    public GlobalModelController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addLoggedInUser(Model model, Authentication authentication) {

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            User user = userRepository.findByEmail(authentication.getName())
                    .orElse(null);

            model.addAttribute("currentUser", user);
        }
    }
}
