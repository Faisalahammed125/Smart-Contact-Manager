package com.smartcontact.smartcontactmanager.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontact.smartcontactmanager.models.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    @Query("from Contact as c where c.user.id = :userId")
    public List<Contact> findContactsByUser(@Param("userId") int userId);
}
