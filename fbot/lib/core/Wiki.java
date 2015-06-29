/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.Credentials;
import fbot.lib.core.Namespace;
import fbot.lib.core.URLBuilder;

public class Wiki {
    protected Credentials settings;

    public Wiki(String user, String px) {
        this(user, px, "commons.wikimedia.org");
    }

    public Wiki(String user, String px, String domain) {
        // first char is upper case usually
        user = Character.toString(user.charAt(0)).toUpperCase()
                + user.substring(1);
        this.settings = new Credentials(user, px);
        this.settings.setTo(domain);
    }

    public synchronized boolean switchDomain(String domain) {
        return this.settings.setTo(domain);
    }

    public synchronized String getCurrentDomain() {
        return this.settings.curr.domain;
    }

    protected synchronized String getToken() {
        return this.settings.curr.edittoken;
    }

    protected synchronized Namespace getNSL() {
        return this.settings.curr.nsl;
    }

    /**
     * Return the user name of the currently logged in user
     * 
     * @return the user name with no namespace prefix
     */
    public String whoami() {
        return this.settings.user;
    }

    protected int getNS(String prefix) {
        return this.getNSL().convert(prefix);
    }

    protected String getNS(int num) {
        return this.getNSL().convert(num);
    }

    public int whichNS(String title) {
        return this.getNSL().whichNS(title);
    }

    public String convertIfNotInNS(String title, String ns) {
        return this.whichNS(title) == this.getNS(ns) ? title : String.format(
                "%s:%s", ns, Namespace.nss(title));
    }

    public boolean isVerified(String domain) {
        return this.settings.cs_archive.containsKey(domain);
    }

    /**
     * Return a new URLBuilder with this wiki's domain
     * 
     * @return the URLBuilder
     */
    protected URLBuilder makeUB() {
        return new URLBuilder(this.getCurrentDomain());
    }
}
