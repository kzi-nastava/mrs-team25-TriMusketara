package com.example.clickanddrive;

public class SessionManager {
    public static final int GUEST = 0;
    public static final int USER = 1;
    public static final int DRIVER = 2;
    public static final int ADMIN = 3;
  
    public static int currentUserType = GUEST;

    public static String token = null;  // JWT token
    public static Long userId = null;   // ID korisnika

    // Fields for following the status of a user
    // If he is blocked or not
    private static boolean isBlocked = false;
    private static String blockReason = null;

    public static void login(String role, String jwtToken, Long id, boolean blocked, String reason) {
        token = jwtToken;
        userId = id;
        isBlocked = blocked;
        blockReason = reason;

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

    public static boolean isUserBlocked() {
        return isBlocked;
    }

    public static String getBlockReason() {
        return blockReason;
    }
}
