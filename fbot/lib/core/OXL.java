/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.commons.WikiGen;
import fbot.lib.core.WMFWiki;

public class OXL {
    public static void main(String[] args) throws Throwable {
        WMFWiki wiki = WikiGen.generate("FSV");
        for (String s : wiki.getUserUploads("Netherzone")) {
            wiki.edit(s, String.valueOf(wiki.getPageText(s)) + "\n{{Subst:npd}}", "npd");
        }
    }
}

