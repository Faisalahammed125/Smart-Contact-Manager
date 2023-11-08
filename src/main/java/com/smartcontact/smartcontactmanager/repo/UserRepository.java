package com.smartcontact.smartcontactmanager.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcontact.smartcontactmanager.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
