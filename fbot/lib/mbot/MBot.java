/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.mbot;

import fbot.lib.core.WMFWiki;
import fbot.lib.core.auxi.Logger;
import fbot.lib.mbot.MAction;
import fbot.lib.mbot.ThreadManager;
import fbot.lib.mbot.WAction;
import java.util.ArrayList;

public class MBot {
    private WMFWiki wiki;
    private int num;

    public MBot(WMFWiki wiki) {
        this(wiki, 20);
    }

    public MBot(WMFWiki wiki, int num) {
        this.wiki = wiki;
        this.num = num;
    }

    public synchronized void setNum(int num) {
        this.num = num;
    }

    public MAction[] start(MAction[] ml) {
        ThreadManager m = new ThreadManager(ml, this.wiki, this.num);
        m.start();
        MAction[] fails = m.getFails();
        if (fails.length > 0) {
            Logger.warn(String.format("MBot failed to process (%d): ", fails.length));
            for (MAction x : fails) {
                Logger.log(x.getTitle(), "PURPLE");
            }
        } else {
            Logger.fyi("MBot completed the task with 0 failures");
        }
        return fails;
    }

    public MAction[] massDelete(String reason, String ... pages) {
        ArrayList<DeleteItem> wl = new ArrayList<DeleteItem>();
        for (String s : pages) {
            wl.add(new DeleteItem(s, reason));
        }
        return this.start(wl.toArray(new DeleteItem[0]));
    }

    public MAction[] massEdit(String reason, String add, String replace, String replacement, String ... pages) {
        ArrayList<EditItem> wl = new ArrayList<EditItem>();
        for (String s : pages) {
            wl.add(new EditItem(s, reason, add, replace, replacement));
        }
        return this.start(wl.toArray(new EditItem[0]));
    }

    public static class DeleteItem
    extends WAction {
        public DeleteItem(String title, String reason) {
            super(title, null, reason);
        }

        @Override
        public boolean doJob(WMFWiki wiki) {
            return wiki.delete(this.getTitle(), this.summary);
        }
    }

    public static class EditItem
    extends WAction {
        private String add;
        private String replace;
        private String replacement;

        public EditItem(String title, String reason, String add, String replace, String replacement) {
            super(title, null, reason);
            this.add = add;
            this.replace = replace;
            this.replacement = replacement;
        }

        @Override
        public boolean doJob(WMFWiki wiki) {
            this.text = wiki.getPageText(this.getTitle());
            if (this.text == null) {
                return false;
            }
            if (this.replace == null && this.add != null) {
                return wiki.edit(this.getTitle(), String.valueOf(this.text) + this.add, this.summary);
            }
            if (this.replace != null && this.replacement != null && this.add != null) {
                return wiki.edit(this.getTitle(), String.valueOf(this.text.replaceAll(this.replace, this.replacement)) + this.add, this.summary);
            }
            if (this.replace != null && this.replacement != null) {
                return wiki.edit(this.getTitle(), this.text.replaceAll(this.replace, this.replacement), this.summary);
            }
            Logger.error(String.format("For '%s', why is everything null?", this.getTitle()));
            return false;
        }
    }

}

