/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.Namespace;
import fbot.lib.core.Reply;
import fbot.lib.core.Request;
import fbot.lib.core.URLBuilder;
import fbot.lib.core.auxi.Logger;

import java.net.CookieManager;
import java.util.HashMap;

public class Credentials {
    protected String user;
    protected String px;
    protected final CookieManager cookiejar = new CookieManager();
    protected final HashMap<String, CredStore> cs_archive = new HashMap<String, CredStore>();
    protected CredStore curr;

    protected Credentials(String user, String px) {
        this.user = user;
        this.px = px;
    }

    protected boolean setTo(String domain) {
        Logger.fyi("Attemting to assign domain to " + domain);
        if (this.cs_archive.containsKey(domain)) {
            this.curr = this.cs_archive.get(domain);
            return true;
        }
        CredStore temp = new CredStore(domain, this);
        if (temp.verify()) {
            this.cs_archive.put(domain, temp);
            this.curr = temp;
            return true;
        }
        return false;
    }

    protected static class CredStore {
        protected String edittoken;
        protected String domain;
        protected Namespace nsl;
        private Credentials sx;

        private CredStore(String domain, Credentials sx) {
            this.domain = domain;
            this.sx = sx;
        }

        private boolean verify() {
            if (this.login(this.domain) && (this.edittoken = this.findEditToken(this.domain)) != null && (this.nsl = this.generateNSL()) != null) {
                return true;
            }
            return false;
        }

        private boolean login(String domain) {
            URLBuilder posttext;
            Reply r;
            URLBuilder ub;
            block4 : {
                Logger.info("Logging in as " + this.sx.user);
                try {
                    ub = new URLBuilder(domain);
                    ub.setAction("login");
                    posttext = new URLBuilder(null);
                    posttext.setParams("lgname", this.sx.user);
                    r = Request.post(ub.makeURL(), posttext.getParamsAsText(), this.sx.cookiejar, null);
                    if (!r.hasError()) break block4;
                    return false;
                }
                catch (Throwable e) {
                    e.printStackTrace();
                    return false;
                }
            }
            if (r.resultIs("NeedToken")) {
                posttext.setParams("lgpassword", this.sx.px, "lgtoken", r.getString("token"));
				try {
					return Request.post(ub.makeURL(), posttext.getParamsAsText(), this.sx.cookiejar, null).resultIs("Success");
				} catch (Exception ex) {
					return false;
				}
            }
            return false;
        }

        private String findEditToken(String domain) {
            Logger.info("Fetching edit token");
            try {
                URLBuilder ub = new URLBuilder(domain);
                ub.setAction("query");
                ub.setParams("prop", "info", "intoken", "edit", "titles", "Fastily");
                return Request.get(ub.makeURL(), this.sx.cookiejar).getString("edittoken");
            }
            catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        }

        private Namespace generateNSL() {
            Logger.info("Generating namespace list");
            try {
                URLBuilder ub = new URLBuilder(this.domain);
                ub.setAction("query");
                ub.setParams("meta", "siteinfo", "siprop", "namespaces");
                return Namespace.makeNamespace(Request.get(ub.makeURL(), this.sx.cookiejar).getJSONObject("namespaces"));
            }
            catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        }

        /* synthetic CredStore(String string, Credentials credentials, CredStore credStore) {
            CredStore credStore2;
            credStore2(string, credentials);
        } */
    }

}

