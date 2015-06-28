/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.auxi.Tuple;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

public class Namespace {
    private HashMap<Object, Tuple<Integer, String>> l = new HashMap<Object, Tuple<Integer, String>>();

    private Namespace() {
    }

    protected static Namespace makeNamespace(JSONObject jo) {
        Namespace ns = new Namespace();
        for (String s : JSONObject.getNames(jo)) {
            JSONObject curr = jo.getJSONObject(s);
            String name = curr.getString("*");
            if (name.isEmpty()) continue;
            Integer id = new Integer(curr.getInt("id"));
            Tuple<Integer, String> t = new Tuple<Integer, String>(id, name);
            ns.l.put(name.toLowerCase(), t);
            ns.l.put(id, t);
        }
        return ns;
    }

    protected int whichNS(String title) {
        int i = title.lastIndexOf(":");
        return i == -1 ? 0 : this.convert(title.substring(0, i));
    }

    protected String convert(int i) {
        return this.convert(new Integer(i));
    }

    protected String convert(Integer i) {
        if (this.l.containsKey(i)) {
            return (String)this.l.get((Object)i).y;
        }
        return i == 0 ? "Main" : null;
    }

    protected int convert(String prefix) {
        String x = prefix.toLowerCase();
        if (this.l.containsKey(x)) {
            return (Integer)this.l.get((Object)x).x;
        }
        if (prefix.equals("") || prefix.equals("Main")) {
            return 0;
        }
        throw new IllegalArgumentException(String.format("'%s' is not a recognized prefix.", prefix));
    }

    protected String prefixToNumString(String prefix) {
        return "" + this.convert(prefix);
    }

    protected String[] prefixToNumStrings(String ... prefixes) {
        ArrayList<String> l = new ArrayList<String>();
        for (String s : prefixes) {
            l.add(this.prefixToNumString(s));
        }
        return l.toArray(new String[0]);
    }

    public static String nss(String title) {
        int i = title.lastIndexOf(58);
        return i > 0 && i + 1 != title.length() ? title.substring(i + 1) : title;
    }
}

