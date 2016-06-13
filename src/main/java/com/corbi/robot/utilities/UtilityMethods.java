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
        String currentDirPath = new File("").getAbsolutePath();
        return currentDirPath.concat(relativePath);
    }
}
