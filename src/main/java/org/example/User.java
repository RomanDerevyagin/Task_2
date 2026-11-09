package org.example;

public class User {
    private String name;
    private String password;
    private String email;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    public User(String email,String password){
        this.email=email;
        this.password=password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
