package com.template;

public class Session {
    private static int currentUserId;
    private static String currentUserRole;
    public static int currentBookingId; // For transport flow

    public static int getCurrentUserId() { return currentUserId; }
    public static void setCurrentUserId(int id) { currentUserId = id; }

    public static String getCurrentUserRole() { return currentUserRole; }
    public static void setCurrentUserRole(String role) { currentUserRole = role; }
}