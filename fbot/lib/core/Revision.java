/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.auxi.JSONParse;
import java.io.PrintStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class Revision {
    private String title;
    private String summary;
    private String user;
    private String text;
    private String timestamp;

    private Revision(String title, JSONObject rev) {
        this.title = title;
        this.timestamp = rev.getString("timestamp");
        this.summary = rev.getString("comment");
        this.text = rev.getString("*");
        this.user = rev.getString("user");
    }

    protected static Revision[] makeRevs(JSONObject reply) {
        ArrayList<Revision> rl = new ArrayList<Revision>();
        String title = "";
        try {
            title = JSONParse.getStringR(reply, "title");
            JSONArray revs = JSONParse.getJSONArrayR(reply, "revisions");
            for (int i = 0; i < revs.length(); ++i) {
                rl.add(new Revision(title, revs.getJSONObject(i)));
            }
            return rl.toArray(new Revision[0]);
        }
        catch (Throwable e) {
            e.printStackTrace();
            System.err.println("Looks like the page, " + title + ", doesn't have revisions");
            return new Revision[0];
        }
    }

    public String getTitle() {
        return this.title;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getUser() {
        return this.user;
    }

    public String getText() {
        return this.text;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String toString() {
        return String.format("----%nTitle:%s%nSummary:%s%nUser:%s%nText:%s%nTimestamp:%s%n----", this.title, this.summary, this.user, this.text, this.timestamp.toString());
    }
}

