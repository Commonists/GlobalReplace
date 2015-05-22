/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.util;

import java.io.PrintStream;
import javax.swing.JOptionPane;

public class FError {
    private FError() {
    }

    public static void errAndExit(String s) {
        FError.errAndExit(s, System.out);
    }

    public static void errAndExit(String s, PrintStream ps) {
        ps.println(s);
        System.exit(1);
    }

    public static void errAndExit(Throwable e) {
        FError.errAndExit(e, "");
    }

    public static void errAndExit(Throwable e, String s) {
        e.printStackTrace();
        System.out.println(s);
        System.exit(1);
    }

    public static void showErrorAndExit(String s, int err) {
        JOptionPane.showMessageDialog(null, s);
        System.exit(err);
    }

    public static void showErrorAndExit(String s) {
        FError.showErrorAndExit(s, 1);
    }

    public static boolean printErrorAndReturn(String err, boolean value) {
        System.err.println(err);
        return value;
    }
}

