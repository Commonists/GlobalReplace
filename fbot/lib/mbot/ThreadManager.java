/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.mbot;

import fbot.lib.core.W;
import fbot.lib.core.auxi.Logger;
import fbot.lib.core.auxi.ProgressTracker;
import fbot.lib.mbot.MAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadManager {
    private final ConcurrentLinkedQueue<MAction> todo = new ConcurrentLinkedQueue();
    private final ConcurrentLinkedQueue<MAction> fails = new ConcurrentLinkedQueue();
    private final ProgressTracker pt;
    private W wiki;
    private int num;

    public ThreadManager(MAction[] ml, W wiki, int num) {
        this.todo.addAll(Arrays.asList(ml));
        this.pt = new ProgressTracker(ml.length);
        this.wiki = wiki;
        this.num = num;
    }

    public void start() {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        int i = 0;
        while (i < Math.min(this.todo.size(), this.num)) {
            Thread t = new Thread(new Job(this));
            threads.add(t);
            t.start();
            ++i;
        }
        for (Thread t : threads) {
            try {
                t.join();
                continue;
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public MAction[] getFails() {
        return this.fails.toArray(new MAction[0]);
    }

    private static class Job
    implements Runnable {
        private ThreadManager m;

        protected Job(ThreadManager m) {
            this.m = m;
        }

        @Override
        public void run() {
            MAction curr;
            if (this.m.todo.peek() == null) {
                return;
            }
            String me = String.valueOf(Thread.currentThread().getName()) + ": ";
            while ((curr = (MAction)this.m.todo.poll()) != null) {
                this.m.pt.inc(me);
                if (!curr.doJob(this.m.wiki)) {
                    this.m.fails.add(curr);
                    continue;
                }
                curr.succeeded = true;
            }
            Logger.fyi(String.valueOf(me) + "There's nothing left for me!");
        }
    }

}

