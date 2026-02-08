package com.example.clickanddrive.dtosample.requests;

public class UserRegistrationRequestDTO {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
    private String address;
    private String phoneNumber;
    private boolean mobile;

    public UserRegistrationRequestDTO(String name, String lastName, String email,
                                      String password, String confirmPassword,
                                      String address, String phoneNumber, boolean mobile) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.mobile = mobile;
    }

    // Getteri i setteri
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public boolean getMobile(){ return mobile; }
    public void setMobile(boolean mobile){ this.mobile = mobile; }
}
