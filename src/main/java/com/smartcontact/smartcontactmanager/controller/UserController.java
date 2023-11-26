package com.smartcontact.smartcontactmanager.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/index")
    public String dashboard(Model model) {
        System.out.println("Here is user Dashboard");
        return "user_dashboard.html";
    }

    @RequestMapping("/add_contact")
    public String OpenContactForm() {
        return "add_contact_form.html";
    }
}
