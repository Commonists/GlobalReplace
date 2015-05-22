/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.util;

import fbot.lib.util.FSystem;
import fbot.lib.util.WikiFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FString {
    private static Random r = new Random();

    public static String capitalize(String s) {
        return s.length() < 2 ? s.toUpperCase() : String.valueOf(s.substring(0, 1).toUpperCase()) + s.substring(1);
    }

    public static String generateRandomFileName(WikiFile file) {
        return String.format("%#o x %s.%s", r.nextInt(255), new SimpleDateFormat("HH.mm.ss").format(new Date()), file.getExtension(false));
    }

    public static String splitGrab(String s, String delim, boolean first, boolean front) {
        int pos;
        int n = pos = first ? s.indexOf(delim) : s.lastIndexOf(delim);
        if (pos == -1) {
            return s;
        }
        return front ? s.substring(0, pos) : s.substring(pos + 1);
    }

    public static /* varargs */ String listCombo(String ... list) {
        String x = "";
        for (String s : list) {
            x = String.valueOf(x) + s + FSystem.lsep;
        }
        return x;
    }

    public static /* varargs */ String concatStringArray(String ... strings) {
        String x = "";
        for (String s : strings) {
            x = String.valueOf(x) + s;
        }
        return x;
    }
}

