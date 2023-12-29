package com.example.pdfcreator.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginEntity {
    private String password;
    private String email;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}