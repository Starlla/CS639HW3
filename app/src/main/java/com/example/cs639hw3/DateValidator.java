package com.example.cs639hw3;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateValidator {
    private Pattern pattern;
    private Matcher matcher;

    private static final String DATE_PATTERN =
            "^(1[0-2]|0?[1-9])/(3[01]|[12][0-9]|0?[1-9])";

    public DateValidator(){
        pattern = Pattern.compile(DATE_PATTERN);
    }

    public boolean validate(final String date){

        matcher = pattern.matcher(date);

        if(matcher.matches()){

            matcher.reset();
            if(matcher.find()){
                String day = matcher.group(2);
                String month = matcher.group(1);

                if (day.equals("31") &&
                        (month.equals("4") || month .equals("6") || month.equals("9") ||
                                month.equals("11") || month.equals("04") || month .equals("06") ||
                                month.equals("09"))) {
                    return false; // only 1,3,5,7,8,10,12 has 31 days
                } else if ((month.equals("2") || month.equals("02"))&&(day.equals("30")||day.equals("31"))) {
                    return false;
                }else
                    return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
