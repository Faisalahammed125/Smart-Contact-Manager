package com.smartcontact.smartcontactmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.smartcontactmanager.helper.Message;
import com.smartcontact.smartcontactmanager.models.User;
import com.smartcontact.smartcontactmanager.repo.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/home")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        System.out.println("Here is about section");
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about.html";
    }

    @RequestMapping("/signup")
    public String signup(Model model) {
        System.out.println("Here is signup");
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup.html";
    }

    // handler for registering user

    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        try {
            if (!agreement) {
                throw new Exception("You have not agreed the terms and condition.");
            }
            if (result.hasErrors()) {
                System.out.println("errors here");
                model.addAttribute("user", user);
                return "signup.html";
            }
            user.setRole("USER");
            user.setEnabled(true);
            // user.setImgurl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            User res = this.userRepository.save(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully Registered !!!",
                    "alert-success"));
            return "signup.html";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong!!!!" + e.getMessage(), "alert-danger"));
            return "signup.html";
        }
    }

    @RequestMapping("/signin")
    public String signin(Model model) {
        model.addAttribute("title", "Sign in - Smart Contact Manager");
        return "signin.html";
    }
}
