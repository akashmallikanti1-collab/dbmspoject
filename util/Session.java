package util;

public class Session {

    public static int currentUserId = -1;
    public static String currentUserRole = null;

    // Clear session on logout
    public static void clearSession() {
        currentUserId = -1;
        currentUserRole = null;
    }

    // Check login status
    public static boolean isLoggedIn() {
        return currentUserId != -1 && currentUserRole != null;
    }
}