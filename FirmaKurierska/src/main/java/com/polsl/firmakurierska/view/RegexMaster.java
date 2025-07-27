package com.polsl.firmakurierska.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMaster {

    private static RegexMaster oneAndOnlyRegexMaster = null;

    private RegexMaster() {};

    public static synchronized RegexMaster getRegexMaster() {
        if (oneAndOnlyRegexMaster == null) {
            oneAndOnlyRegexMaster = new RegexMaster();
        }

        return oneAndOnlyRegexMaster;
    }

    /**
     * Validates string for names, surnames, etc.
     * @param textToCheck string to check with regex pattern.
     * @return true if string consists of letters and starts with a uppercase.
     */
    public boolean checkStringForNames(String textToCheck) {

        Pattern pattern = Pattern.compile("^[A-Z][a-z]{1,23}$");
        Matcher matcher = pattern.matcher(textToCheck);

        return matcher.find();
    }
    
    /**
     * Validates string for PESEL.
     * @param textToCheck string to check with regex pattern.
     * @return true if string consists of eleven digits.
     */
    public boolean checkStringForPESEL(String textToCheck) {
        
        Pattern pattern = Pattern.compile("^\\d{11}$");
        Matcher matcher = pattern.matcher(textToCheck);

        return matcher.find();
    }

    /**
     * Validates for login.
     * @param textToCheck string to check with regex pattern.
     * @return true when string starts with letter and consists of letters, numbers, '-' and '_' symbols.
     */
    public boolean checkStringForLogin(String textToCheck) {
        
        Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_-]{1,23}$");
        Matcher matcher = pattern.matcher(textToCheck);

        return matcher.find();
    }

    /**
     * Validates string for password.
     * @param textToCheck string to check with regex pattern.
     * @return true when password doesn't have .
     */
    public boolean checkStringForPassword(String textToCheck) {
        
        Pattern pattern = Pattern.compile("^(?!.* ).{1,24}$");
        Matcher matcher = pattern.matcher(textToCheck);

        return matcher.find();
    }

    /**
     * Validates string with letters and numbers.
     * @param textToCheck string to check with regex pattern.
     * @return true when string consists of letters, numbers, '-' and '_'.
     */
    public boolean checkStringForLettersAndNumbers(String textToCheck) {
        
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{1,24}$");
        Matcher matcher = pattern.matcher(textToCheck);

        return matcher.find();
    }

    /**
     * Validates string for weight, vehicle capacity and other real numbers.
     * @param textToCheck string to check with regex pattern.
     * @return true when string can be converted to double.
     */
    public boolean checkStringForDouble(String textToCheck) {
        
        Pattern pattern = Pattern.compile("^\\d{1,5}(\\.\\d{1,5})?$");
        Matcher matcher = pattern.matcher(textToCheck);

        return matcher.find();
    }
}
