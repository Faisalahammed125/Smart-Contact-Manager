package com.smartcontact.smartcontactmanager.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;
    private String name;
    private String nickname;
    private String work;
    private String email;
    private String phone;
    private String imgurl;
    @Column(length = 5000)
    private String description;

    @ManyToOne
    private User user;

    public Contact() {
        super();
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return this.cId == ((Contact) obj).getcId();
    }

    // @Override
    // public String toString() {
    // return "Contact [cId=" + cId + ", name=" + name + ", nickname=" + nickname +
    // ", work=" + work + ", email="
    // + email + ", phone=" + phone + ", imgurl=" + imgurl + ", description=" +
    // description + ", user=" + user
    // + "]";
    // }

}
