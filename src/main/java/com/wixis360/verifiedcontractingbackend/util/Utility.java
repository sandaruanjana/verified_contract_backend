package com.wixis360.verifiedcontractingbackend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utility {
    private Utility() {
        throw new IllegalStateException("Utility class");
    }

    public static String convertSimpleDateFormat(Date date){
        Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static String encodePassword(String password){
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder(12);
        return passwordEncoder.encode(password);
    }

    public static Boolean checkPassword(String password,String encodedPassword){
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        return passwordEncoder.matches(password,encodedPassword);
    }

    public static Date incrementDate(Date date,int days){
        return new Date(date.getTime() + TimeUnit.DAYS.toMillis(days));
    }

    public static String convertDouble(Double value) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setGroupingUsed(false);
        nf.setMinimumFractionDigits(2);
        return nf.format(value);
    }
}
