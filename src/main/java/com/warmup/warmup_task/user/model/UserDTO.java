package com.warmup.warmup_task.user.model;

import java.time.LocalDateTime;


public class UserDTO {
    private String username;
    private String email;
    private String status;
    private LocalDateTime lastLoginDate;
    private String password;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getLastLoginDate(){
        return lastLoginDate;
    }
    public void setLastLoginDate(LocalDateTime lastLoginDate){
        this.lastLoginDate = lastLoginDate;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
