package com.finix.framework.util;

public class NumberUtil {

    public static int parseInt(String intStr, int defaultValue) {
        try {
            return Integer.parseInt(intStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long parseLong(String longStr, long defaultValue){
        try {
            return Long.parseLong(longStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * return positive int value of originValue
     * @param originValue
     * @return positive int
     */
    public static int getPositive(int originValue){
        return 0x7fffffff & originValue;
    }
}