/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.util;

import fbot.lib.util.FError;
import fbot.lib.util.FSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ReadFile {
    private ArrayList<String> l = new ArrayList();

    public ReadFile(File f) {
        this(f, FSystem.getDefaultCharset());
    }

    public ReadFile(String f) {
        this(new File(f));
    }

    public ReadFile(File f, String enc) {
        Scanner m = null;
        try {
            m = new Scanner(f, enc);
        }
        catch (Throwable e) {
            FError.errAndExit(e);
        }
        while (m.hasNextLine()) {
            this.l.add(m.nextLine().trim());
        }
    }

    public String[] getList() {
        return this.l.toArray(new String[0]);
    }

    public HashMap<String, String> getSplitList(String delim) {
        HashMap<String, String> h = new HashMap<String, String>();
        for (String s : this.l) {
            int i = s.indexOf(delim);
            if (i <= -1) continue;
            h.put(s.substring(0, i), s.substring(i + 1));
        }
        return h;
    }

    public String getTextAsBlock() {
        String x = "";
        for (String s : this.l) {
            x = String.valueOf(x) + s + FSystem.lsep;
        }
        return x;
    }

    public String toString() {
        String x = "";
        for (String s : this.l) {
            x = String.valueOf(x) + s + "\n";
        }
        return x;
    }
}

