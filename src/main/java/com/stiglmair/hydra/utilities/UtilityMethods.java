package com.stiglmair.hydra.utilities;

import com.stiglmair.hydra.main.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * This class provides Utility Methods that are required through all packages
 *
 * @author PogChamp
 */
public class UtilityMethods {

    public enum Emote {
        KAPPA, KREYGASM, POGCHAMP, RESIDENTSLEEPER, TRIHARD,
        TWITCH, FAILFISH, SMILEY, KAPPAPRIDE, FEELSBADMAN,
        BIBLETHUMP, MINGLEE, PJSALT, SAYAN, FOURHEAD, HAHAA,
        KEEPO, KAPPAROSS, KAPPAXPLOSION, ANELE, TYLERENGINE
    }

    /**
     * Returns the first non-null argument, or null if all arguments are null
     * or if no arguments are specified.
     *
     * @param args Zero or more arguments.
     * @return A non-null argument, unless all arguments are null.
     */
    public static <T> T firstNonNull(T... args) {
        for (T v: args) {
            if (v != null) {
                return v;
            }
        }
        return null;
    }

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
     *
     * @param s String to be converted
     * @return String s, which will be underlined in discord
     */
    public static String highlightUnderline(String s) {
        return "__" + s + "__";
    }

    /**
     * Converts a String so that it will be displayed italic in discord
     *
     * @param s String to beconverted
     * @return String s, which will be italic in discord
     */
    public static String highlightItalic(String s) {
        return "*" + s + "*";
    }

    /**
     * Converts a String so that it will be displayed bold in discord
     *
     * @param s String to be converted
     * @return String s, which will be bold in discord
     */
    public static String highlightBold(String s) {
        return "**" + s + "**";
    }

    /**
     * deletes a file or folder taken from
     * http://stackoverflow.com/questions/3775694/deleting-folder-from-java by
     * user Sean Patrick Floyd
     *
     * @param path path to file or folder
     * @throws IOException
     */
    public static void deleteFileOrFolder(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(final Path file, final IOException e) {
                return handleException(e);
            }

            private FileVisitResult handleException(final IOException e) {
                Main.logger.error("Error occured while trying to delete a file or folder.", e);
                return TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                    throws IOException {
                if (e != null) {
                    return handleException(e);
                }
                Files.delete(dir);
                return CONTINUE;
            }
        });
    }

    /**
     * Creates a folder. If the folder already exists, it will be deleted
     * before it is created anew.
     *
     * @param path path to folder
     * @throws IOException
     */
    public static void ensureEmptyFolder(String path) throws IOException {
        File f = new File(path);
        if (!(f.exists() && f.isDirectory())) {
            // TODO: Handle failure of mkdir.
            f.mkdir();
        } else {
            // Deletes the logging folder and creates a new one, thus wiping its content
            deleteFileOrFolder(f.toPath());
            // TODO: Handle failure of mkdir.
            f.mkdir();
        }
    }

    public static String getEmote(Emote emote) {
        String parsedEmote = ":";
        switch (emote) {
            case KAPPA:
                parsedEmote += "Kappa";
                break;
            case KREYGASM:
                parsedEmote += "Kreygasm";
                break;
            case POGCHAMP:
                parsedEmote += "PogChamp";
                break;
            case RESIDENTSLEEPER:
                parsedEmote += "ResidentSleeper";
                break;
            case TRIHARD:
                parsedEmote += "TriHard";
                break;
            case TWITCH:
                parsedEmote += "Twitch";
                break;
            case FAILFISH:
                parsedEmote += "FailFish";
                break;
            case SMILEY:
                parsedEmote += "Smiley";
                break;
            case KAPPAPRIDE:
                parsedEmote += "KappaPride";
                break;
            case FEELSBADMAN:
                parsedEmote += "FeelsBadMan";
                break;
            case BIBLETHUMP:
                parsedEmote += "BibleThump";
                break;
            case MINGLEE:
                parsedEmote += "MingLee";
                break;
            case PJSALT:
                parsedEmote += "PJSalt";
                break;
            case SAYAN:
                parsedEmote += "Sayan";
                break;
            case FOURHEAD:
                parsedEmote += "4Head";
                break;
            case HAHAA:
                parsedEmote += "haHaa";
                break;
            case KEEPO:
                parsedEmote += "Keepo";
                break;
            case KAPPAROSS:
                parsedEmote += "KappaRoss";
                break;
            case KAPPAXPLOSION:
                parsedEmote += "KappaXplosion";
                break;
            case ANELE:
                parsedEmote += "Anele";
                break;
            case TYLERENGINE:
                parsedEmote += "TylerEngine";
                break;
            default:
                parsedEmote = "";
        }
        if (parsedEmote.isEmpty()) {
            return parsedEmote;
        } else {
            parsedEmote += ":";
            return parsedEmote;
        }
    }
}
