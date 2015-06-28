/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.Constants;
import fbot.lib.core.Tools;
import fbot.lib.core.auxi.JSONParse;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class Reply {
    private JSONObject reply;
    private String error = null;
    private String result;
    private static final List<String> whitelist = Arrays.asList("NeedToken", "Success", "Continue");

    protected Reply(InputStream is) {
        this.reply = new JSONObject(Tools.inputStreamToString(is, true));
        this.result = this.getString("result");
        if (Constants.debug) {
            System.out.println(this.reply.toString());
        }
        if (this.reply.has("error") || this.result != null && !whitelist.contains(this.result)) {
            this.error = this.reply.toString();
            System.err.println("ERROR: " + this.error);
        }
    }

    protected JSONObject getReply() {
        return this.reply;
    }

    protected boolean hasError() {
        if (this.error != null) {
            return true;
        }
        return false;
    }

    protected boolean hasErrorIgnore(String ... codes) {
        if (!this.hasError()) {
            return false;
        }
        String ec = this.getString("code");
        if (ec != null) {
            for (String code : codes) {
                if (!code.equals(ec)) continue;
                return false;
            }
        }
        return true;
    }

    protected String getError() {
        return this.error;
    }

    protected String getString(String key) {
        return JSONParse.getStringR(this.reply, key);
    }

    protected JSONObject getJSONObject(String key) {
        return JSONParse.getJSONObjectR(this.reply, key);
    }

    protected int getInt(String key) {
        return JSONParse.getIntR(this.reply, key);
    }

    protected JSONArray getJSONArray(String key) {
        return JSONParse.getJSONArrayR(this.reply, key);
    }

    protected String getResult() {
        return this.result;
    }

    protected boolean resultIs(String code) {
        return code.equals(this.getResult());
    }
}

