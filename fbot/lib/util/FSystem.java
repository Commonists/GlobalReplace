/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FSystem {
    public static final String LINE_SEP = System.getProperty("line.separator");
    public static final String FILE_SEP = File.separator;
    public static final String HOME_DIR = System.getProperty("user.home");

    private FSystem() {
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").contains("Windows");
    }

    public static String getDefaultCharset() {
        return FSystem.isWindows() ? "US-ASCII" : "UTF-8";
    }

    public static String getScriptHeader() {
        return FSystem.isWindows() ? "@echo off" : "#!/bin/bash\n" + LINE_SEP;
    }

    public static void copyFile(String src, String dest) throws IOException {
        int len;
        FileInputStream in = new FileInputStream(new File(src));
        FileOutputStream out = new FileOutputStream(new File(dest));
        byte[] buf = new byte[1024];
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
