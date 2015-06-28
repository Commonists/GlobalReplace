/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.Contrib;
import fbot.lib.core.ImageInfo;
import fbot.lib.core.Reply;
import fbot.lib.core.Request;
import fbot.lib.core.Revision;
import fbot.lib.core.Tools;
import fbot.lib.core.URLBuilder;
import fbot.lib.core.Wiki;
import fbot.lib.core.auxi.JSONParse;
import fbot.lib.core.auxi.Logger;
import fbot.lib.core.auxi.Tuple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class FQuery {
    private FQuery() {
    }

    protected static JSONObject[] fatQuery(URLBuilder ub, int max, String limString, String contString, boolean isStr, Wiki wiki) {
        boolean unlim = max < 0;
        ArrayList<JSONObject> jl = new ArrayList<JSONObject>();
        int fetch_num = 500;
        try {
            for (int completed = 0; completed < max || unlim; completed+=fetch_num) {
                if (!(unlim || max - completed >= 500)) {
                    fetch_num = max - completed;
                }
                ub.setParams(limString, "" + fetch_num);
                Reply r = Request.get(ub.makeURL(), wiki.settings.cookiejar);
                if (!r.hasError()) {
                    JSONObject reply = r.getReply();
                    jl.add(reply);
                    if (reply.has("query-continue")) {
                        String[] arrstring = new String[2];
                        arrstring[0] = contString;
                        arrstring[1] = Tools.enc(isStr ? JSONParse.getStringR(reply, contString) : "" + JSONParse.getIntR(reply, contString));
                        ub.setParams(arrstring);
                        continue;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            return new JSONObject[0];
        }
        return jl.toArray(new JSONObject[0]);
    }

    protected static JSONObject[] groupQuery(URLBuilder ub, String parentKey, Wiki wiki, String titlekey, String ... titles) {
        ArrayList<JSONObject> jl = new ArrayList<JSONObject>();
        try {
            for (String[] tl : Tools.splitStringArray(20, titles)) {
                ub.setParams(titlekey, Tools.enc(Tools.fenceMaker("|", tl)));
                Reply r = Request.get(ub.makeURL(), wiki.settings.cookiejar);
                if (!r.hasError()) {
                    JSONObject parent = JSONParse.getJSONObjectR(r.getReply(), parentKey);
                    if (parent == null) continue;
                    for (String s : JSONObject.getNames(parent)) {
                        jl.add(parent.getJSONObject(s));
                    }
                    continue;
                } else {
                    break;
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            return new JSONObject[0];
        }
        return jl.toArray(new JSONObject[0]);
    }

    /**
     * Return the page text of the given page
     * @param wiki the wiki to connect to
     * @param title the title of the page to read the text from
     * @return null or the page text
     */
    public static String getPageText(Wiki wiki, String title) {
        Revision[] rl = FQuery.getRevisions(wiki, title, 1, false);
        return rl.length >= 1 && rl[0] != null ? rl[0].getText() : null;
    }

    public static Revision[] getRevisions(Wiki wiki, String title, int num, boolean olderfirst) {
        Logger.info("Fetching revisions of " + title);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("query");
        String[] arrstring = new String[8];
        arrstring[0] = "prop";
        arrstring[1] = "revisions";
        arrstring[2] = "rvprop";
        arrstring[3] = URLBuilder.chainProps("timestamp", "user", "comment", "content");
        arrstring[4] = "rvdir";
        arrstring[5] = olderfirst ? "newer" : "older";
        arrstring[6] = "titles";
        arrstring[7] = Tools.enc(title);
        ub.setParams(arrstring);
        ArrayList<Revision> rl = new ArrayList<Revision>();
        for (JSONObject jo : FQuery.fatQuery(ub, num, "rvlimit", "rvcontinue", false, wiki)) {
            rl.addAll(Arrays.asList(Revision.makeRevs(jo)));
        }
        return rl.toArray(new Revision[0]);
    }

    public static String[] getCategoryMembers(Wiki wiki, String cat, int max, String ... ns) {
        String title = wiki.whichNS(cat) == 0 ? String.format("%s:%s", wiki.getNS(14), cat) : cat;
        Logger.info("Fetching category members of " + title);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("query");
        ub.setParams("list", "categorymembers", "cmtitle", Tools.enc(title));
        if (ns.length > 0) {
            ub.setParams("cmnamespace", Tools.enc(Tools.fenceMaker("|", wiki.getNSL().prefixToNumStrings(ns))));
        }
        ArrayList<String> l = new ArrayList<String>();
        for (JSONObject jo : FQuery.fatQuery(ub, max, "cmlimit", "cmcontinue", true, wiki)) {
            JSONArray jl = JSONParse.getJSONArrayR(jo, "categorymembers");
            for (int i = 0; i < jl.length(); ++i) {
                l.add(jl.getJSONObject(i).getString("title"));
            }
        }
        return l.toArray(new String[0]);
    }

    public static String[] getLinksOnPage(Wiki wiki, String title, String ... ns) {
        Logger.info("Fetching page links of " + title);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("query");
        ub.setParams("prop", "links", "titles", Tools.enc(title));
        if (ns.length > 0) {
            ub.setParams("plnamespace", Tools.enc(Tools.fenceMaker("|", wiki.getNSL().prefixToNumStrings(ns))));
        }
        ArrayList<String> l = new ArrayList<String>();
        for (JSONObject jo : FQuery.fatQuery(ub, -1, "pllimit", "plcontinue", true, wiki)) {
            JSONArray jl = JSONParse.getJSONArrayR(jo, "links");
            if (jl == null) break;
            for (int i = 0; i < jl.length(); ++i) {
                l.add(jl.getJSONObject(i).getString("title"));
            }
        }
        return l.toArray(new String[0]);
    }

    public static Contrib[] getContribs(Wiki wiki, String user, int max, String ... ns) {
        Logger.info("Fetching contribs of " + user);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("query");
        ub.setParams("list", "usercontribs", "ucuser", Tools.enc(user));
        if (ns.length > 0) {
            ub.setParams("ucnamespace", Tools.enc(Tools.fenceMaker("|", wiki.getNSL().prefixToNumStrings(ns))));
        }
        ArrayList<Contrib> l = new ArrayList<Contrib>();
        for (JSONObject jo : FQuery.fatQuery(ub, max, "uclimit", "ucstart", true, wiki)) {
            l.addAll(Arrays.asList(Contrib.makeContribs(jo)));
        }
        return l.toArray(new Contrib[0]);
    }

    public static int getCategorySize(Wiki wiki, String title) {
        Reply r;
        block3 : {
            Logger.info("Fetching category size of " + title);
            URLBuilder ub = wiki.makeUB();
            ub.setAction("query");
            ub.setParams("prop", "categoryinfo", "titles", Tools.enc(title));
            try {
                r = Request.get(ub.makeURL(), wiki.settings.cookiejar);
                if (!r.hasError()) break block3;
                return -1;
            }
            catch (Throwable e) {
                e.printStackTrace();
                return -1;
            }
        }
        return r.getInt("size");
    }
    
    /**
     * Expand the specified wiki markup by passing it to the MediaWiki parser
     * through the API.
     * @param wiki the wiki to connect to
     * @param text
     *            the markup to expand
     * @return the parsed markup as wikitext
     */
    public static String expandtemplates(Wiki wiki, String text) {
        // This is a POST because markup can be arbitrarily large, as in the
        // size of an article (over 10kb).
        Logger.fyi("Expand templates: " + text);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("expandtemplates");
        ub.setParams("prop", "wikitext");
        try {
            Reply r = Request.post(ub.makeURL(),
                    URLBuilder.chainParams("text", Tools.enc(text)),
                    wiki.settings.cookiejar,
                    "application/x-www-form-urlencoded");

            if (!r.hasError()) {
                return r.getString("wikitext");
            } else {
                return null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] imageUsage(Wiki wiki, String file) {
        if (wiki.whichNS(file) != wiki.getNS("File")) {
            System.err.println("'%s' must be a valid filename and include the 'File:' prefix");
            return new String[0];
        }
        Logger.info("Fetching image usage of " + file);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("query");
        ub.setParams("list", "imageusage", "iutitle", Tools.enc(file));
        ArrayList<String> l = new ArrayList<String>();
        for (JSONObject jo : FQuery.fatQuery(ub, -1, "iulimit", "iucontinue", true, wiki)) {
            JSONArray jl = JSONParse.getJSONArrayR(jo, "imageusage");
            for (int i = 0; i < jl.length(); ++i) {
                l.add(jl.getJSONObject(i).getString("title"));
            }
        }
        return l.toArray(new String[0]);
    }

    public static String[] getImagesOnPage(Wiki wiki, String title) {
        Logger.info("Fetching images linked to " + title);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("query");
        ub.setParams("prop", "images", "titles", Tools.enc(title));
        ArrayList<String> l = new ArrayList<String>();
        for (JSONObject jo : FQuery.fatQuery(ub, -1, "imlimit", "imcontinue", true, wiki)) {
            JSONArray jl = JSONParse.getJSONArrayR(jo, "images");
            if (jl == null) continue;
            for (int i = 0; i < jl.length(); ++i) {
                l.add(jl.getJSONObject(i).getString("title"));
            }
        }
        return l.toArray(new String[0]);
    }

    public static List<Tuple<String, Boolean>> exists(Wiki wiki, String ... titles) {
        Logger.info("Checking to see if some pages exist");
        URLBuilder ub = wiki.makeUB();
        ub.setAction("query");
        ub.setParams("prop", "pageprops", "ppprop", "missing");
        ArrayList<Tuple<String, Boolean>> l = new ArrayList<Tuple<String, Boolean>>();
        for (JSONObject jo : FQuery.groupQuery(ub, "pages", wiki, "titles", titles)) {
            boolean flag = JSONParse.getStringR(jo, "missing") == null;
            l.add(new Tuple<String, Boolean>(JSONParse.getStringR(jo, "title"), new Boolean(flag)));
        }
        return l;
    }

    public static String[] getUserUploads(Wiki wiki, String user) {
        Logger.info("Grabbing uploads of User:" + user);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("query");
        ub.setParams("list", "allimages", "aisort", "timestamp", "aiuser", Tools.enc(user));
        ArrayList<String> l = new ArrayList<String>();
        for (JSONObject jo : FQuery.fatQuery(ub, -1, "ailimit", "aistart", true, wiki)) {
            JSONArray jl = JSONParse.getJSONArrayR(jo, "allimages");
            for (int i = 0; i < jl.length(); ++i) {
                l.add(jl.getJSONObject(i).getString("title"));
            }
        }
        return l.toArray(new String[0]);
    }

    public static ImageInfo getImageInfo(Wiki wiki, String title, int height, int width) {
        if (wiki.whichNS(title) != wiki.getNS("File")) {
            return null;
        }
        Logger.info("Fetching image info for " + title);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("query");
        ub.setParams("prop", "imageinfo", "iiprop", Tools.enc("url|size"), "titles", Tools.enc(title));
        if (height > 0 && width > 0) {
            ub.setParams("iiurlheight", "" + height, "iiurlwidth", "" + width);
        }
        try {
            Reply r = Request.get(ub.makeURL(), wiki.settings.cookiejar);
            JSONArray ja = r.getJSONArray("imageinfo");
            return ja == null ? null : new ImageInfo(ja.getJSONObject(0));
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getTemplatesOnPage(Wiki wiki, String title) {
        Logger.info("Fetching templates on " + title);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("query");
        ub.setParams("prop", "templates", "titles", Tools.enc(title));
        ArrayList<String> l = new ArrayList<String>();
        for (JSONObject jo : FQuery.fatQuery(ub, -1, "tllimit", "tlcontinue", true, wiki)) {
            JSONArray jl = JSONParse.getJSONArrayR(jo, "templates");
            for (int i = 0; i < jl.length(); ++i) {
                l.add(jl.getJSONObject(i).getString("title"));
            }
        }
        return l.toArray(new String[0]);
    }

    public static ArrayList<Tuple<String, String>> globalUsage(Wiki wiki, String title) {
        if (wiki.whichNS(title) != wiki.getNS("File")) {
            return null;
        }
        ArrayList<Tuple<String, String>> l = new ArrayList<Tuple<String, String>>();
        Logger.info("Fetching global usage of " + title);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("query");
        ub.setParams("prop", "globalusage", "guprop", "namespace", "titles", Tools.enc(title));
        for (JSONObject jo : FQuery.fatQuery(ub, -1, "gulimit", "gucontinue", true, wiki)) {
            JSONArray ja = JSONParse.getJSONArrayR(jo, "globalusage");
            for (int i = 0; i < ja.length(); ++i) {
                JSONObject curr = ja.getJSONObject(i);
                l.add(new Tuple<String, String>(curr.getString("title"), curr.getString("wiki")));
            }
        }
        return l;
    }

}

