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

    public boolean checkStringForNames(String textToCheck) {

        Pattern pattern = Pattern.compile("^[A-Z][a-z]{1,5}$");
        Matcher matcher = pattern.matcher(textToCheck);

        return matcher.find();
    }
    
}
