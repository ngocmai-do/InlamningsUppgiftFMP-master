package com.example.inlamningsuppgiftfmp.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public class CustomerDto {
    private Long id;

    @NotBlank(message = "Name shall not be blank")
    private String name;

    @NotBlank(message = "Email shall not be blank")
    @Email(message = "Not valid email address")
    private String email;

    @NotBlank(message = "Telephone number shall not be blank")
    private String tel;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

}
