package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;


import java.math.BigInteger;

public class Info {
    private Long id;
    private String name;
    private String gender;
    private BigInteger age;
    private BigInteger userType;
    private String email;
    private String avatar;


    private String password;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public BigInteger getAge() {
        return age;
    }

    public void setAge(BigInteger age) {
        this.age = age;
    }

    public BigInteger getUserType() {
        return userType;
    }

    public void setUserType(BigInteger userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }



}
