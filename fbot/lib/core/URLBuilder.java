/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.Tools;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class URLBuilder {
    private String domain;
    private String base;
    private String action;
    private HashMap<String, String> pl = new HashMap();

    public URLBuilder(String domain) {
        this.setDomain(domain);
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
        this.base = String.format("https://%s/w/api.php?format=json&action=", domain);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public /* varargs */ void setParams(String ... params) {
        if (params.length % 2 == 1) {
            throw new UnsupportedOperationException("params cannot be odd # of elements: " + params.length);
        }
        for (int i = 0; i < params.length; i+=2) {
            this.pl.put(params[i], params[i + 1]);
        }
    }

    public URL makeURL() {
        try {
            return new URL(String.valueOf(this.base) + this.action + URLBuilder.chainParams(this.getParamsAsList()));
        }
        catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[] getParamsAsList() {
        ArrayList<String> hold = new ArrayList<String>();
        for (Map.Entry<String, String> e : this.pl.entrySet()) {
            hold.add(e.getKey());
            hold.add(e.getValue());
        }
        return hold.toArray(new String[0]);
    }

    public String getParamsAsText() {
        return URLBuilder.chainParams(this.getParamsAsList());
    }

    public void clearParams() {
        this.pl = new HashMap();
    }

    public void clearAction() {
        this.action = null;
    }

    public void clearAll() {
        this.clearParams();
        this.clearAction();
    }

    public static /* varargs */ String chainParams(String ... params) {
        if (params.length % 2 == 1) {
            throw new UnsupportedOperationException("params contains an odd number of elements:" + params.length);
        }
        String x = "";
        for (int i = 0; i < params.length; i+=2) {
            x = String.valueOf(x) + String.format("&%s=%s", params[i], params[i + 1]);
        }
        return x;
    }

    public static /* varargs */ String chainProps(String ... props) {
        String x = "";
        if (props.length >= 1) {
            x = String.valueOf(x) + props[0];
        }
        for (int i = 1; i < props.length; ++i) {
            x = String.valueOf(x) + "|" + props[i];
        }
        return Tools.enc(x);
    }
}

