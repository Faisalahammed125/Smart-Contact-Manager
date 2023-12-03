package com.smartcontact.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.*;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontact.smartcontactmanager.helper.Message;
import com.smartcontact.smartcontactmanager.models.Contact;
import com.smartcontact.smartcontactmanager.models.User;
import com.smartcontact.smartcontactmanager.repo.ContactRepository;
import com.smartcontact.smartcontactmanager.repo.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @ModelAttribute
    public void CommonData(Model model, Principal principal) {
        String email = principal.getName();
        User user = userRepository.getUserbyUserName(email);
        if (user != null) {
            model.addAttribute("user", user);
            System.out.println(user);
        }
    }

    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard - Smart Contact Manager");
        System.out.println("Here is user Dashboard");
        return "user_dashboard.html";
    }

    @RequestMapping("/add_contact")
    public String OpenContactForm(Model model, Principal principal) {
        model.addAttribute("title", "Add Contact - Smart Contact Manager");
        return "add_contact_form";
    }

    @RequestMapping(value = "/process_contact", method = RequestMethod.POST)
    public String ProcessContactForm(@ModelAttribute Contact contact,
            @RequestParam("image") MultipartFile file, Principal principal,
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        try {
            String email = principal.getName();
            User user = this.userRepository.getUserbyUserName(email);

            /// processing and uploading file..

            if (file.isEmpty()) {
                contact.setImgurl("default_user.jpg");
            } else {
                contact.setImgurl(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            contact.setUser(user);
            user.getContacts().add(contact);

            this.userRepository.save(user);
            session.setAttribute("message", new Message("Contact Successfully Added !!!",
                    "alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong!!!!" + e.getMessage(), "alert-danger"));
        }
        return "add_contact_form.html";
    }

    // show contact section

    @RequestMapping("/show_contact")
    public String ShowContact(Model model, Principal principal) {
        model.addAttribute("title", "Show Contact - Smart Contact Manager");
        String email = principal.getName();
        User user = this.userRepository.getUserbyUserName(email);
        List<Contact> contacts = this.contactRepository.findContactsByUser(user.getId());
        model.addAttribute("contacts", contacts);
        return "view_contact.html";
    }

    @RequestMapping("/{cId}/contact")
    public String ShowContactDetails(@PathVariable("cId") int cId, Model model, Principal principal) {
        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();

        String email = principal.getName();
        User user = this.userRepository.getUserbyUserName(email);
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("title", contact.getName());
            model.addAttribute("contact", contact);
        }
        return "contact_details.html";
    }

    @RequestMapping("/delete/{cId}")
    public String DeleteContact(@PathVariable("cId") int cId, Model model,
            Principal principal,
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        Contact contact = this.contactRepository.findById(cId).get();

        User user = this.userRepository.getUserbyUserName(principal.getName());
        user.getContacts().remove(contact);
        this.userRepository.save(user);

        session.setAttribute("message", new Message("Contact Successfully Deleted !!!",
                "alert-success"));
        return "redirect:/user/show_contact";
    }

    @RequestMapping("/update_contact/{cId}")
    public String UpdateContact(@PathVariable("cId") int cId, Model model) {
        model.addAttribute("title", "Update Contact");
        Contact contact = this.contactRepository.findById(cId).get();
        model.addAttribute("contact", contact);
        return "update_form.html";
    }

    @RequestMapping(value = "/process_update", method = RequestMethod.POST)
    public String UpdateHandler(@ModelAttribute Contact contact,
            @RequestParam("image") MultipartFile file, Principal principal,
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        try {
            String email = principal.getName();
            User user = this.userRepository.getUserbyUserName(email);

            Contact oldContact = this.contactRepository.findById(contact.getcId()).get();

            /// processing and uploading file..

            if (file.isEmpty()) {
                contact.setImgurl(oldContact.getImgurl());
            } else {
                File deletFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deletFile, oldContact.getImgurl());
                file1.delete();

                contact.setImgurl(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            contact.setUser(user);

            this.contactRepository.save(contact);
            session.setAttribute("message", new Message("Contact Successfully Updated !!!",
                    "alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong!!!!" + e.getMessage(), "alert-danger"));
        }
        return "redirect:/user/" + contact.getcId() + "/contact";
    }

    @RequestMapping("/profile_view")
    public String UserProfile(Model model, Principal principal) {
        User user = this.userRepository.getUserbyUserName(principal.getName());
        model.addAttribute("title", user.getName());
        model.addAttribute("user", user);
        return "user_profile.html";
    }

    @RequestMapping("/setting")
    public String Setting(Model model, Principal principal) {
        model.addAttribute("title", "Setting - Smart Contact Manager");
        User user = this.userRepository.getUserbyUserName(principal.getName());
        model.addAttribute("user", user);
        return "setting.html";
    }

    @RequestMapping(value = "/update_profile", method = RequestMethod.POST)
    public String ProfileUpdate(@RequestParam("name") String name, @RequestParam("about") String about,
            @RequestParam("Password") String Password,
            @RequestParam("image") MultipartFile file, Principal principal,
            Model model,
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = this.userRepository.getUserbyUserName(principal.getName());
        try {

            /// processing and uploading file..

            if (file.isEmpty()) {
                //
            } else {
                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile, user.getImgurl());
                file1.delete();

                user.setImgurl(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            if (Password.isEmpty()) {
                throw new Exception("Enter Password to confirm changes!!");
            }
            if (!(this.passwordEncoder.matches(Password, user.getPassword()))) {
                throw new Exception("Password incorrect!!");
            }
            user.setName(name);
            user.setAbout(about);

            this.userRepository.save(user);
            session.setAttribute("message", new Message("User information Succesfully Updated !!!",
                    "alert-success"));
            return "redirect:/user/profile_view";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
            return "setting.html";
        }
    }

    @RequestMapping("/change_password")
    public String ChangePassword(Model model) {
        model.addAttribute("title", "Change Password");
        return "passwordchange.html";
    }

    @RequestMapping(value = "/process_updatepassword", method = RequestMethod.POST)
    public String PasswordUpdate(@RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Principal principal, Model model,
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        model.addAttribute("title", "Change Password");
        User user = this.userRepository.getUserbyUserName(principal.getName());
        try {

            /// processing and uploading file..
            if (!(this.passwordEncoder.matches(oldPassword, user.getPassword()))) {
                throw new Exception("Old Password isn't matched!!");
            }
            if (!(confirmPassword.equals(newPassword))) {
                throw new Exception("New Password and Confirm Password aren't matched!!");
            }
            user.setPassword(this.passwordEncoder.encode(newPassword));

            this.userRepository.save(user);
            session.setAttribute("message", new Message("Succesfully Password Updated !!!",
                    "alert-success"));
            return "redirect:/user/profile_view";
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong!!!!" + e.getMessage(), "alert-danger"));
            return "passwordchange.html";
        }
    }
}
