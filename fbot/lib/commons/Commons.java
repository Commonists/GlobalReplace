/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.commons;

import fbot.lib.commons.WikiGen;
import fbot.lib.core.Contrib;
import fbot.lib.core.W;
import fbot.lib.mbot.MAction;
import fbot.lib.mbot.MBot;
import fbot.lib.mbot.WAction;
import fbot.lib.util.ReadFile;
import java.util.ArrayList;
import java.util.Arrays;

public class Commons {
    public static final W com = WikiGen.generate("FSV");

    private Commons() {
    }

    public static String[] nukeFastilyTest(boolean exit) {
        ArrayList<String> fails = new ArrayList<String>();
        fails.addAll(Arrays.asList(Commons.categoryNuke("Fastily Test", "Uploader requested deletion of a recently uploaded and unused file", false, new String[0])));
        fails.addAll(Arrays.asList(Commons.nukeUploads("FSVI", "Uploader requested deletion of a recently uploaded and unused file")));
        if (exit) {
            System.exit(0);
        }
        return fails.toArray(new String[0]);
    }

    public static String[] clearCopyVios() {
        return Commons.categoryNuke("Copyright violations", "[[Commons:Licensing|Copyright violation]]: If you are the copyright holder/author and/or have authorization to upload the file, email [[COM:OTRS]]", false, "File");
    }

    public static /* varargs */ String[] clearOSD(String reason, String ... ns) {
        return Commons.categoryNuke("Other speedy deletions", reason, false, ns);
    }

    public static /* varargs */ String[] categoryNuke(String cat, String reason, boolean delCat, String ... ns) {
        String[] fails = Commons.nuke(reason, com.getCategoryMembers(cat, ns));
        if (delCat && com.getCategorySize(cat) == 0) {
            WikiGen.generate("Fastily").delete(cat, "Empty category");
        }
        return fails;
    }

    public static String[] drDel(String dr) {
        return Commons.nukeLinksOnPage(dr, "[[" + dr + "]]", "File");
    }

    public static /* varargs */ String[] nukeEmptyFiles(String ... files) {
        ArrayList<WAction> l = new ArrayList<WAction>();
        for (String s : files) {
            l.add(new WAction(s, null, "File page with no file uploaded"){

                @Override
                public boolean doJob(W wiki) {
                    return wiki.getImageInfo(this.getTitle()) == null ? wiki.delete(this.getTitle(), this.summary) : true;
                }
            });
        }
        return Commons.doAction("Fastily", l.toArray(new WAction[0]));
    }

    public static /* varargs */ String[] emptyCatDel(String ... cats) {
        ArrayList<WAction> l = new ArrayList<WAction>();
        for (String s : cats) {
            l.add(new WAction(s, null, "Empty category"){

                @Override
                public boolean doJob(W wiki) {
                    return wiki.getCategorySize(this.getTitle()) <= 0 ? wiki.delete(this.getTitle(), this.summary) : true;
                }
            });
        }
        return Commons.doAction("Fastily", l.toArray(new WAction[0]));
    }

    public static /* varargs */ String[] nukeContribs(String user, String reason, String ... ns) {
        ArrayList<String> l = new ArrayList<String>();
        for (Contrib c : com.getContribs(user, ns)) {
            l.add(c.getTitle());
        }
        return Commons.nuke(reason, l.toArray(new String[0]));
    }

    public static String[] nukeUploads(String user, String reason) {
        return Commons.nuke(reason, com.getUserUploads(user));
    }

    public static /* varargs */ String[] nukeLinksOnPage(String title, String reason, String ... ns) {
        return Commons.nuke(reason, com.getLinksOnPage(title, ns));
    }

    public static /* varargs */ String[] nuke(String reason, String ... pages) {
        return MAction.convertToString(WikiGen.genM("Fastily").massDelete(reason, pages));
    }

    public static /* varargs */ String[] doAction(String user, WAction ... pages) {
        return MAction.convertToString(WikiGen.genM(user).start(pages));
    }

    public static /* varargs */ String[] nuke(String reason, String ns, String ... pages) {
        int ni = com.whichNS(ns);
        ArrayList<String> todo = new ArrayList<String>();
        for (String s : pages) {
            if (com.whichNS(s) != ni) continue;
            todo.add(s);
        }
        return Commons.nuke(reason, todo.toArray(new String[0]));
    }

    public static String[] nukeFromFile(String path, String reason) {
        return Commons.nuke(reason, new ReadFile(path).getList());
    }

    public static /* varargs */ String[] removeDelete(String reason, String ... titles) {
        return MAction.convertToString(new MBot(com).massEdit(reason, "", "(?si)\\{\\{(delete).*?\\}\\}", "", titles));
    }

    public static /* varargs */ String[] removeLSP(String reason, String ... titles) {
        return MAction.convertToString(new MBot(com).massEdit(reason, "", "(?si)\\{\\{(speedy|no permission|no license|no source).*?\\}\\}", "", titles));
    }

    public static /* varargs */ String[] addText(String reason, String text, String ... titles) {
        return MAction.convertToString(new MBot(com).massEdit(reason, text, null, null, titles));
    }

}

