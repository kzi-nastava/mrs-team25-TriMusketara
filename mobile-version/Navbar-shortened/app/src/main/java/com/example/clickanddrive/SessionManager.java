package com.example.clickanddrive;

public class SessionManager {
    public static final int GUEST = 0;
    public static final int USER = 1;
    public static final int DRIVER = 2;
    public static final int ADMIN = 3;

    public static int currentUserType = GUEST;
    public static String token = null;  // JWT token
    public static Long userId = null;   // ID korisnika

    public static void login(String role, String jwtToken, Long id) {
        token = jwtToken;
        userId = id;

        switch (role.toLowerCase()) {
            case "user":
            case "passenger":
                currentUserType = USER;
                break;
            case "driver":
                currentUserType = DRIVER;
                break;
            case "admin":
                currentUserType = ADMIN;
                break;
            default:
                currentUserType = GUEST;
        }
    }

    public static void logout() {
        currentUserType = GUEST;
        token = null;
        userId = null;
    }
}
