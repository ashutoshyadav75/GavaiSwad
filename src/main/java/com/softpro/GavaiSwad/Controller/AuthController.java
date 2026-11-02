package com.softpro.GavaiSwad.Controller;

import java.util.concurrent.ThreadLocalRandom;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.softpro.GavaiSwad.Service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model, HttpSession session) {

        generateCaptcha(model, session);

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam Integer captchaAnswer,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Object storedCaptcha = session.getAttribute("captchaAnswer");

        if (!(storedCaptcha instanceof Integer)
                || !storedCaptcha.equals(captchaAnswer)) {

            session.removeAttribute("captchaAnswer");

            model.addAttribute("error", "Incorrect CAPTCHA. Please try again.");
            generateCaptcha(model, session);

            return "register";
        }

        session.removeAttribute("captchaAnswer");

        if (fullName.isBlank()
                || email.isBlank()
                || password.length() < 8) {

            model.addAttribute(
                    "error",
                    "Please enter valid details. Password must be at least 8 characters."
            );

            generateCaptcha(model, session);
            return "register";
        }

        try {
            userService.registerUser(fullName, email, password, phone);

            redirectAttributes.addFlashAttribute(
                    "registered",
                    "Registration successful! Your account is awaiting admin approval."
            );

            return "redirect:/login?registered";

        } catch (IllegalArgumentException ex) {

            model.addAttribute("error", ex.getMessage());
            generateCaptcha(model, session);

            return "register";
        }
    }

    private void generateCaptcha(Model model, HttpSession session) {

        int number1 = ThreadLocalRandom.current().nextInt(1, 10);
        int number2 = ThreadLocalRandom.current().nextInt(1, 10);

        session.setAttribute("captchaAnswer", number1 + number2);

        model.addAttribute(
                "captchaQuestion",
                number1 + " + " + number2 + " = ?"
        );
    }
}