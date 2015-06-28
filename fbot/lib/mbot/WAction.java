/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.mbot;

import fbot.lib.core.WMFWiki;
import fbot.lib.mbot.MAction;

public abstract class WAction extends MAction {
    protected String title;
    protected String text;
    protected String summary;

    protected WAction(String title, String text, String summary) {
        super(title);
        this.text = text;
        this.summary = summary;
    }

    @Override
    public abstract boolean doJob(WMFWiki var1);

    @Override
    public String toString() {
        return String.format("(title: %s | text: %s | reason: %s)", this.title,
                this.text, this.summary);
    }
}
