/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.utilities;

import java.io.File;
import java.util.Scanner;

/**
 * This class provides Utility Methods that are required through all packages
 *
 * @author PogChamp
 */
public class UtilityMethods {

    /**
     * converts a relative path to an absolute one
     *
     * @param relativePath a relative path, to which the absolutePath should be
     * created
     * @return an absolute path
     */
    public static String generatePath(String relativePath) {
        String currentDirPath = new File("").getAbsolutePath() + "/";
        return currentDirPath.concat(relativePath);
    }

    /**
     *
     * @param time in milliseconds that should be converted
     * @return a String, e.g like this: 24 Tag(e), 15 Stunde(n), 13 Minute(n), 1
     * Sekunde(n)
     */
    public static String formatTime(long time) {
        long second = (time / 1000) % 60;
        long minute = (time / (1000 * 60)) % 60;
        long hour = (time / (1000 * 60 * 60)) % 24;
        long day = (time / (1000 * 60 * 60 * 24));
        return String.format("%d Tag(e), %02d Stunde(n), %02d Minute(n) und %02d Sekunde(n)", day, hour, minute, second);
    }
    /**
     * function to check whether a given String is an Integer taken from
     * http://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
     * by user Petúr
     *
     * @param s String which is to be checked
     * @return true if given string is of type int, false otherwise
     */
    public static boolean isInteger(String s) {
        int radix = 10;
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }
            if (Character.digit(s.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }
    /**
     * Converts a String so that it will be displayed underlined in discord
     * @param s String to be converted
     * @return String s, which will be underlined in discord
     */
    public static String highlightStringUnderline(String s)
    {
        return "__" + s + "__";
    }
    /**
     * Converts a String so that it will be displayed italic in discord
     * @param s String to beconverted
     * @return String s, which will be italic in discord
     */
    public static String highlightStringItalic(String s)
    {
        return "*" + s + "*";
    }
    /**
     * Converts a String so that it will be displayed bold in discord
     * @param s String to be converted
     * @return String s, which will be bold in discord
     */
    public static String highlightStringBold(String s)
    {
        return "**" + s + "**";
    }
}
