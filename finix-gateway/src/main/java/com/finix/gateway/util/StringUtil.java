package com.finix.gateway.util;

public class StringUtil {


    public static char[] toCharArray(String password) {
        return password == null ? "".toCharArray() : password.toCharArray();
    }
    
    public static String sanitise(String input) {
        return input.replaceAll("\\n", "\\\\n");
    }
    
}
