/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.Contrib;
import fbot.lib.core.FAction;
import fbot.lib.core.FQuery;
import fbot.lib.core.ImageInfo;
import fbot.lib.core.Revision;
import fbot.lib.core.Wiki;
import fbot.lib.core.auxi.Tuple;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class W
extends Wiki {
    public W(String user, String px) {
        super(user, px);
    }

    public W(String user, String px, String domain) {
        super(user, px, domain);
    }

    public boolean edit(String title, String text, String reason) {
        return FAction.edit(this, title, text, reason);
    }

    public boolean undo(String title, String reason) {
        return FAction.undo(this, title, reason);
    }

    public boolean nullEdit(String title) {
        return this.edit(title, this.getPageText(title), "null edit");
    }

    public boolean purge(String title) {
        return FAction.purge(this, title);
    }

    public String getPageText(String title) {
        return FQuery.getPageText(this, title);
    }

    public Revision[] getRevisions(String title, int num, boolean olderfirst) {
        return FQuery.getRevisions(this, title, num, olderfirst);
    }

    public Revision[] getRevisions(String title) {
        return this.getRevisions(title, -1, false);
    }

    public boolean delete(String title, String reason) {
        return FAction.delete(this, title, reason);
    }

    public boolean undelete(String title, String reason) {
        return FAction.undelete(this, title, reason);
    }

    public int getCategorySize(String title) {
        return FQuery.getCategorySize(this, title);
    }

    public /* varargs */ String[] getCategoryMembers(String title, String ... ns) {
        return this.getCategoryMembers(title, -1, ns);
    }

    public /* varargs */ String[] getCategoryMembers(String title, int max, String ... ns) {
        return FQuery.getCategoryMembers(this, title, max, ns);
    }

    public /* varargs */ String[] getLinksOnPage(String title, String ... ns) {
        return FQuery.getLinksOnPage(this, title, ns);
    }

    public /* varargs */ String[] getValidLinksOnPage(String title, String ... ns) {
        return this.exists(this.getLinksOnPage(title, ns), true);
    }

    public /* varargs */ Contrib[] getContribs(String user, int max, String ... ns) {
        return FQuery.getContribs(this, user, max, ns);
    }

    public /* varargs */ Contrib[] getContribs(String user, String ... ns) {
        return this.getContribs(user, -1, ns);
    }

    public String[] getUserUploads(String user) {
        return FQuery.getUserUploads(this, user);
    }

    public String[] imageUsage(String file) {
        return FQuery.imageUsage(this, file);
    }

    public String[] getImagesOnPage(String title) {
        return FQuery.getImagesOnPage(this, title);
    }

    public boolean exists(String title) {
        return (Boolean)this.exists((String[])new String[]{title}).get((int)0).y;
    }

    public List<Tuple<String, Boolean>> exists(String[] titles) {
        return FQuery.exists(this, titles);
    }

    public String[] exists(String[] titles, boolean e) {
        ArrayList<String> l = new ArrayList<String>();
        for (Tuple<String, Boolean> t : this.exists(titles)) {
            if ((Boolean)t.y ^ e) continue;
            l.add((String)t.x);
        }
        return l.toArray(new String[0]);
    }

    public ImageInfo getImageInfo(String title) {
        return this.getImageInfo(title, -1, -1);
    }

    public ImageInfo getImageInfo(String title, int height, int width) {
        return FQuery.getImageInfo(this, title, height, width);
    }

    public String[] getTemplatesOnPage(String title) {
        return FQuery.getTemplatesOnPage(this, title);
    }

    public boolean upload(File f, String title, String text, String reason) {
        return FAction.upload(this, f, title, text, reason);
    }

    public ArrayList<Tuple<String, String>> globalUsage(String title) {
        return FQuery.globalUsage(this, title);
    }
}

