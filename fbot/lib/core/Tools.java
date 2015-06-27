/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Tools {
    /**
     * Translate unsafe characters using UTF-8 encoding scheme
     * @param s the text to encode
     * @return
     */
    public static String enc(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (Throwable e) {
            e.printStackTrace();
            return s;
        }
    }

    protected static /* varargs */ String[] massEnc(String ... strings) {
        ArrayList<String> l = new ArrayList<String>();
        for (String s : strings) {
            l.add(Tools.enc(s));
        }
        return l.toArray(new String[0]);
    }

    public static String inputStreamToString(InputStream is, boolean close) {
        try {
            String line;
            String x = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = in.readLine()) != null) {
                x = String.valueOf(x) + line + "\n";
            }
            if (close) {
                is.close();
            }
            return x.trim();
        }
        catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String capitalize(String s) {
        return s.length() < 2 ? s.toUpperCase() : String.valueOf(s.substring(0, 1).toUpperCase()) + s.substring(1);
    }

    public static /* varargs */ String fenceMaker(String post, String ... planks) {
        if (planks.length == 0) {
            return "";
        }
        String x = planks[0];
        for (int i = 1; i < planks.length; ++i) {
            x = String.valueOf(x) + post + planks[i];
        }
        return x;
    }

    public static /* varargs */ String[][] splitStringArray(int max, String ... strings) {
        ArrayList<String[]> l = new ArrayList<String[]>();
        if (strings.length <= max) {
            return new String[][]{strings};
        }
        int overflow = strings.length % max;
        for (int i = 0; i < strings.length - overflow; i+=max) {
            l.add(Arrays.copyOfRange(strings, i, i + max));
        }
        if (overflow > 0) {
            l.add(Arrays.copyOfRange(strings, strings.length - overflow, strings.length));
        }
        return (String[][])l.toArray(new String[0][]);
    }

    protected static /* varargs */ HashMap<String, Object> makeParamMap(Object ... ol) {
        HashMap<String, Object> l = new HashMap<String, Object>();
        if (ol.length % 2 == 1) {
            return null;
        }
        for (int i = 0; i < ol.length; i+=2) {
            if (!(ol[i] instanceof String)) continue;
            l.put((String)ol[i], ol[i + 1]);
        }
        return l;
    }

    public static boolean closeInputStream(InputStream is) {
        try {
            is.close();
            return true;
        }
        catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
}

