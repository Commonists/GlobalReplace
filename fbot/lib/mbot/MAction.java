/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.mbot;

import fbot.lib.core.W;
import java.util.ArrayList;

public abstract class MAction {
    protected String title;
    protected boolean succeeded = false;

    protected MAction(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean didSucceed() {
        return this.succeeded;
    }

    public String toString() {
        return String.format("(title: %s | succeeded: %b)", this.title, this.succeeded);
    }

    public static /* varargs */ String[] convertToString(MAction ... actions) {
        ArrayList<String> l = new ArrayList<String>();
        for (MAction m : actions) {
            l.add(m.title);
        }
        return l.toArray(new String[0]);
    }

    public abstract boolean doJob(W var1);
}

