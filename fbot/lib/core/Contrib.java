/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.Constants;
import fbot.lib.core.auxi.JSONParse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Contrib {
    private String user;
    private String title;
    private String summary;
    private Date timestamp;
    private int revid;
    private int parentid;

    private Contrib(JSONObject jo) throws JSONException, ParseException {
        this.user = jo.getString("user");
        this.title = jo.getString("title");
        this.summary = jo.getString("comment");
        this.timestamp = Constants.sdf.parse(jo.getString("timestamp"));
        this.revid = jo.getInt("revid");
        this.parentid = jo.getInt("parentid");
    }

    protected static Contrib[] makeContribs(JSONObject reply) {
        ArrayList<Contrib> l = new ArrayList<Contrib>();
        try {
            JSONArray jl = JSONParse.getJSONArrayR(reply, "usercontribs");
            for (int i = 0; i < jl.length(); ++i) {
                l.add(new Contrib(jl.getJSONObject(i)));
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            return new Contrib[0];
        }
        return l.toArray(new Contrib[0]);
    }

    public String getUser() {
        return this.user;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSummary() {
        return this.summary;
    }

    public Date getDate() {
        return this.timestamp;
    }

    public int getRevId() {
        return this.revid;
    }

    public int getParentId() {
        return this.parentid;
    }

    public String toString() {
        return String.format("----%nUser: %s%nTitle: %s%nSummary: %s%nTimestamp: %s%nRevID:%d%nParentID: %d%n----", this.user, this.title, this.summary, this.timestamp.toString(), this.revid, this.parentid);
    }
}

