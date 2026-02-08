package com.example.clickanddrive.dtosample.responses;

public class UserProfileResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private String address;
    private String phone;
    private String profileImageUrl;

    public UserProfileResponseDTO() {}

    public UserProfileResponseDTO(Long id, String email, String name, String surname,
                                  String address, String phone, String profileImageUrl) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
    }

    // Getteri i setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
