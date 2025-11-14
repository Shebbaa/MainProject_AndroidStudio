package com.example.isp291_folomeevstepan;

public class Utils {
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        phone = phone.replaceAll("[\\s\\-()]", "");
        return phone.matches("^\\+?\\d{10,15}$");
    }

    public static boolean isValidPassword(String pass) {
        if (pass == null) return false;
        if (pass.length() < 6) return false;
        if (!pass.matches(".*\\d.*")) return false;
        if (!pass.matches(".*[A-Za-z].*")) return false;
        return true;
    }
}
