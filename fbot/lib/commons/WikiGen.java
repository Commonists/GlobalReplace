/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.commons;

import fbot.lib.core.WMFWiki;
import fbot.lib.mbot.MBot;
import fbot.lib.util.FError;
import fbot.lib.util.FSystem;
import fbot.lib.util.ReadFile;

import java.io.File;
import java.util.HashMap;

public class WikiGen {
    private static final HashMap<String, String> px = WikiGen.genX();
    private static final HashMap<String, WMFWiki> cache = new HashMap<String, WMFWiki>();

    private WikiGen() {
    }

    private static HashMap<String, String> genX() {
        for (String s : new String[]{String.valueOf(FSystem.HOME_DIR) + FSystem.FILE_SEP + ".px.txt", ".px.txt"}) {
            if (!new File(s).exists()) continue;
            return new ReadFile(s).getSplitList(":");
        }
        FError.errAndExit(".px.txt not found in either home or classpath");
        return null;
    }

    public static WMFWiki generate(String user, String domain) {
        if (cache.containsKey(user)) {
            WMFWiki wiki = cache.get(user);
            if (wiki.getCurrentDomain().equals(domain)) {
                return wiki;
            }
            if (wiki.switchDomain(domain)) {
                return wiki;
            }
        } else {
            WMFWiki wiki = user.equals("Fastily") ? new WMFWiki(user, px.get("FP"), domain) : new WMFWiki(user, px.get("FSP"), domain);
            if (wiki.isVerified(domain)) {
                cache.put(user, wiki);
                return wiki;
            }
        }
        return null;
    }

    public static WMFWiki generate(String user) {
        return WikiGen.generate(user, "commons.wikimedia.org");
    }

    public static MBot genM(String user, String domain) {
        return new MBot(WikiGen.generate(user, domain));
    }

    public static MBot genM(String user) {
        return new MBot(WikiGen.generate(user));
    }

    public static MBot genM(String user, int threads) {
        return new MBot(WikiGen.generate(user), threads);
    }
}

