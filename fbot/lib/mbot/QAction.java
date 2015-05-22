/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.mbot;

import fbot.lib.core.W;
import fbot.lib.core.auxi.Tuple;
import fbot.lib.mbot.MAction;

public abstract class QAction
extends MAction {
    protected Tuple<String, ?> result;

    protected QAction(String title) {
        super(title);
    }

    public Tuple<String, ?> getResult() {
        return this.result;
    }

    @Override
    public abstract boolean doJob(W var1);
}

