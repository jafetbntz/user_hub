package dev.bntz.models;

import java.util.UUID;



public class User {
    private UUID id;
    private String fullName;
    private String whatsApp;
    private String email;
    private RolesEnum role;

    public User() {
        this.role =  RolesEnum.CLIENT;
    }

    public UUID getId() {
        return this.id;
    }

    public String getWhatsApp() {
        return whatsApp;
    }

    public void setWhatsApp(String whatsApp) {
        this.whatsApp = whatsApp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RolesEnum getRole() {
        return role;
    }

    public void setRole(RolesEnum role) {
        this.role = role;
    }
    
    public void setId(UUID newID) {
        this.id = newID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


}
