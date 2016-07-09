/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.utilities;

import java.io.File;

/**
 * This class provides Utility Methods that are required through all packages
 * @author PogChamp
 */
public class UtilityMethods {
    /**
     * converts a relative path to an absolute one
     * @param relativePath a relative path, to which the absolutePath should be created
     * @return an absolute path
     */
    public static String generatePath(String relativePath)
    {
        String currentDirPath = new File("").getAbsolutePath() + "/";
        return currentDirPath.concat(relativePath);
    }
    /**
     * 
     * @param time in milliseconds that should be converted
     * @return a String, e.g like this: 24 Tag(e), 15 Stunde(n), 13 Minute(n), 1 Sekunde(n)
     */
    public static String formatTime(long time)
    {
        long second = (time / 1000) % 60;
        long minute = (time / (1000 * 60)) % 60;
        long hour = (time / (1000 * 60 * 60)) % 24;
        long day = (time / (1000 * 60 * 60 * 24));
        return String.format("%d Tag(e), %02d Stunde(n), %02d Minute(n) und %02d Sekunde(n)", day, hour, minute, second);
    }
}
