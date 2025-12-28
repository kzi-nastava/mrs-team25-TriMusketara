package com.example.clickanddrive;

public class SessionManager {
    public static final int GUEST = 0;
    public static final int USER = 1;
    public static final int DRIVER = 2;
    public static final int ADMIN = 3;

    public static int currentUserType = ADMIN;
}
