package com.smartcontact.smartcontactmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.smartcontact.smartcontactmanager.models.User;
import com.smartcontact.smartcontactmanager.repo.UserRepository;

@Transactional
public class UserDetailsServiceimpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserbyUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Couldnot found user!!");
        }
        // System.out.println(user);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return customUserDetails;
    }

}
