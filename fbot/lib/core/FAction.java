/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.Credentials;
import fbot.lib.core.FQuery;
import fbot.lib.core.Namespace;
import fbot.lib.core.Reply;
import fbot.lib.core.Request;
import fbot.lib.core.Revision;
import fbot.lib.core.Tools;
import fbot.lib.core.URLBuilder;
import fbot.lib.core.Wiki;
import fbot.lib.core.auxi.Logger;
import fbot.lib.util.FError;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.CookieManager;
import java.net.URL;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class FAction {
    private static final int chunksize = 4194304;

    private FAction() {
    }

    public static boolean edit(Wiki wiki, String title, String text, String reason) {
        Logger.info("Editing " + title);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("edit");
        String[] es = Tools.massEnc(title, text, reason, wiki.getToken());
        String posttext = URLBuilder.chainParams("title", es[0], "text", es[1], "summary", es[2], "token", es[3]);
        try {
            return Request.post(ub.makeURL(), posttext, wiki.settings.cookiejar, "application/x-www-form-urlencoded").resultIs("Success");
        }
        catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean undo(Wiki wiki, String title, String reason) {
        Logger.fyi("Undoing newest revision of " + title);
        try {
            Revision[] rl = FQuery.getRevisions(wiki, title, 2, false);
            return rl.length < 2 ? FError.printErrorAndReturn("There are fewer than two revisions in " + title, false) : FAction.edit(wiki, title, rl[1].getText(), reason);
        }
        catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean purge(Wiki wiki, String title) {
        Logger.fyi("Purging " + title);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("purge");
        ub.setParams("titles", Tools.enc(title));
        try {
            Reply r = Request.get(ub.makeURL(), wiki.settings.cookiejar);
            if (!r.hasError() && r.getJSONArray("purge").getJSONObject(0).has("purged")) {
                return true;
            }
            return false;
        }
        catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(Wiki wiki, String title, String reason) {
        Logger.info("Deleting " + title);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("delete");
        String[] es = Tools.massEnc(title, reason, wiki.getToken());
        String posttext = URLBuilder.chainParams("title", es[0], "reason", es[1], "token", es[2]);
        try {
            return !Request.post(ub.makeURL(), posttext, wiki.settings.cookiejar, "application/x-www-form-urlencoded").hasErrorIgnore("missingtitle");
        }
        catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean undelete(Wiki wiki, String title, String reason) {
        Logger.info("Restoring " + title);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("undelete");
        String[] es = Tools.massEnc(title, reason, wiki.getToken());
        String posttext = URLBuilder.chainParams("title", es[0], "reason", es[1], "token", es[2]);
        try {
            return !Request.post(ub.makeURL(), posttext, wiki.settings.cookiejar, "application/x-www-form-urlencoded").hasError();
        }
        catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean upload(Wiki wiki, File f, String title, String text, String reason) {
        Logger.info(String.format("Uploading '%s' to '%s'", f.getName(), title));
        String uploadTo = wiki.convertIfNotInNS(title, "File");
        long filesize = f.length();
        if (filesize <= 0) {
            System.err.println(String.format("'%s' is an empty file.", f.getName()));
            return false;
        }
        long chunks = filesize / 0x400000 + (((long)filesize % 0x400000 > 0) ? 1 : 0);
        URLBuilder ub = wiki.makeUB();
        ub.setAction("upload");
        String filekey = null;
        FileInputStream in = null;
        String filename = Namespace.nss(uploadTo);
        HashMap<String, Object> l = Tools.makeParamMap("filename", filename, "token", wiki.getToken(), "ignorewarnings", "true", "stash", "1", "filesize", "" + filesize);
        try {
            in = new FileInputStream(f);
            int i = 0;
            while ((long)i < chunks) {
                Logger.log(String.format("(%s): Uploading chunk %d of %d", f.getName(), i + 1, chunks), "PURPLE");
                l.put("offset", "" + i * 4194304);
                if (filekey != null) {
                    l.put("filekey", filekey);
                }
                if ((filekey = FAction.uploadChunk(l, wiki, ub, f, in, i + 1)) == null) {
                    throw new IOException("Server is being difficult today");
                }
                ++i;
            }
            in.close();
            return filekey != null ? FAction.unstash(wiki, filekey, filename, text, reason) : false;
        }
        catch (Throwable e) {
            e.printStackTrace();
            if (filekey != null) {
                FAction.unstash(wiki, filekey, filename, text, reason);
            }
            Tools.closeInputStream(in);
            return false;
        }
    }

    private static String uploadChunk(HashMap<String, Object> l, Wiki wiki, URLBuilder ub, File f, FileInputStream in, int id) throws IOException {
        int remain = in.available();
        byte[] chunk = remain > 4194304 ? new byte[4194304] : new byte[remain];
        in.read(chunk);
        l.put("chunk\"; filename=\"" + f.getName(), chunk);
        Reply r = null;
        for (int i = 0; i < 5; ++i) {
            try {
                r = Request.chunkPost(ub.makeURL(), l, wiki.settings.cookiejar);
                break;
            }
            catch (Throwable e) {
                e.printStackTrace();
                Logger.error(String.format("(%s): Encountered error @ chunk %d.  Retrying...", f.getName(), id));
            }
        }
        return !r.hasError() ? r.getString("filekey") : null;
    }

    private static boolean unstash(Wiki wiki, String filekey, String title, String text, String reason) {
        Logger.info(String.format("Unstashing '%s' from temporary archive @ '%s'", title, filekey));
        URLBuilder ub = wiki.makeUB();
        ub.setAction("upload");
        String[] es = Tools.massEnc(title, text, reason, wiki.getToken(), filekey);
        String posttext = URLBuilder.chainParams("filename", es[0], "text", es[1], "comment", es[2], "ignorewarnings", "true", "filekey", es[4], "token", es[3]);
        try {
            return Request.post(ub.makeURL(), posttext, wiki.settings.cookiejar, "application/x-www-form-urlencoded").resultIs("Success");
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

