/*
 * Decompiled with CFR 0_101.
 */
package fbot.ft;

import fbot.lib.commons.Commons;
import fbot.lib.commons.WikiGen;
import fbot.lib.core.W;
import fbot.lib.mbot.MAction;
import fbot.lib.mbot.MBot;
import fbot.lib.mbot.QAction;
import fbot.lib.mbot.WAction;
import fbot.lib.util.FCLI;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class DRArchive {
    private static ConcurrentLinkedQueue<String> singles = new ConcurrentLinkedQueue();
    private static final String stamp = "\\d{2}?:\\d{2}?, \\d{1,}? (January|February|March|April|May|June|July|August|September|October|November|December) \\d{4}?";

    public static void main(String[] args) {
        CommandLine l = DRArchive.parseArgs(args);
        if (l.hasOption('c')) {
            ArrayList<ProcDR> dl = new ArrayList<ProcDR>();
            for (String s : Commons.com.getTemplatesOnPage("User:Fastily/SingletonDR")) {
                if (!s.startsWith("Commons:Deletion requests/")) continue;
                dl.add(new ProcDR(s));
            }
            Commons.doAction("Fastily", dl.toArray(new ProcDR[0]));
        } else {
            Commons.com.nullEdit("User:FSV/DL");
            ArrayList<ProcLog> pl = new ArrayList<ProcLog>();
            for (String s : Commons.com.getValidLinksOnPage("User:FSV/DL", new String[0])) {
                pl.add(new ProcLog(s));
            }
            WikiGen.genM("FSV", 5).start(pl.toArray(new ProcLog[0]));
            String x = "Report generated @ ~~~~~\n";
            for (String s2 : singles) {
                x = String.valueOf(x) + String.format("%n{{%s}}", s2);
            }
            Commons.com.edit("User:Fastily/SingletonDR", x, "Update report");
        }
    }

    private static CommandLine parseArgs(String[] args) {
        Options ol = new Options();
        ol.addOption("c", false, "If this is set, close all DRs of 'User:Fastily/SingletonDR'");
        ol.addOption("help", false, "Print this help message and exit");
        return FCLI.gnuParse(ol, args, "DRArchive [-c]");
    }

    private static class DRItem
    extends QAction {
        private String text;
        private boolean isSingle;
        private boolean canA;

        private DRItem(String title) {
            super(title);
            this.isSingle = false;
            this.canA = false;
        }

        @Override
        public boolean doJob(W wiki) {
            this.text = wiki.getPageText(this.getTitle());
            this.canArchive();
            if (!this.canA) {
                this.isSingleton(wiki);
            }
            return true;
        }

        private void canArchive() {
            if (this.text == null) {
                this.canA = false;
            } else {
                String temp = this.text.replaceAll("(?i)\\[\\[(Category:).+?\\]\\]", "");
                temp = temp.replaceAll("(?si)\\<(includeonly)\\>.*?\\</(includeonly)\\>", "").trim();
                this.canA = temp.matches("(?si)\\{\\{(delh|DeletionHeader).*?\\}\\}.*?\\{(DeletionFooter/Old|Delf|DeletionFooter|Udelf).*?\\}\\}");
            }
        }

        private void isSingleton(W wiki) {
            this.isSingle = this.text != null && !this.text.matches("(?si).*?\\{\\{(delh|DeletionHeader|DeletionFooter/Old|Delf|DeletionFooter|Udelf).*?\\}\\}.*?") && !this.text.matches(String.format("(?si).*?%s.*?%s.*?", "\\d{2}?:\\d{2}?, \\d{1,}? (January|February|March|April|May|June|July|August|September|October|November|December) \\d{4}?", "\\d{2}?:\\d{2}?, \\d{1,}? (January|February|March|April|May|June|July|August|September|October|November|December) \\d{4}?")) && wiki.getLinksOnPage(this.getTitle(), "File").length == 1;
        }

        /* synthetic DRItem(String string, DRItem dRItem) {
            DRItem dRItem2;
            dRItem2(string);
        } */
    }

    private static class ProcDR
    extends WAction {
        private ProcDR(String title) {
            super(title, null, String.format("[[%s]]", title));
        }

        @Override
        public boolean doJob(W wiki) {
            Commons.nukeLinksOnPage(this.getTitle(), this.summary, "File");
            this.text = wiki.getPageText(this.getTitle());
            return this.text != null ? wiki.edit(this.getTitle(), String.format("{{delh}}%n%s%n----%n'''Deleted''' -~~~~%n{{delf}}", this.text), "deleted") : false;
        }

        /* synthetic ProcDR(String string, ProcDR procDR) {
            ProcDR procDR2;
            procDR2(string);
        } */
    }

    private static class ProcLog
    extends WAction {
        private String archive;

        private ProcLog(String title) {
            super(title, null, "Archiving %d threads %s [[%s]]");
            this.archive = "Commons:Deletion requests/Archive" + title.substring(title.indexOf(47));
        }

        @Override
        public boolean doJob(W wiki) {
            MAction[] l = this.fetchDRs(wiki);
            new MBot(wiki, 10).start(l);
            ArrayList<String> toArchive = new ArrayList<String>();
            for (MAction d : l) {
                if (((DRItem)d).canA) {
                    toArchive.add(d.getTitle());
                    continue;
                }
                if (!((DRItem)d).isSingle) continue;
                singles.add(d.getTitle());
            }
            String[] al = toArchive.toArray(new String[0]);
            if (al.length > 0) {
                wiki.edit(this.getTitle(), this.extract(wiki.getPageText(this.getTitle()), al), String.format(this.summary, toArchive.size(), "to", this.archive));
                wiki.edit(this.archive, String.valueOf(wiki.getPageText(this.archive)) + this.pool(al), String.format(this.summary, toArchive.size(), "from", this.getTitle()));
            }
            return true;
        }

        private /* varargs */ String pool(String ... titles) {
            String x = "";
            for (String s : titles) {
                x = String.valueOf(x) + String.format("%n{{%s}}", s);
            }
            return x;
        }

        private /* varargs */ String extract(String base, String ... titles) {
            String x = base;
            for (String s : titles) {
                x = x.replace((CharSequence)("\n{{" + s + "}}"), (CharSequence)"");
            }
            return x;
        }

        private DRItem[] fetchDRs(W wiki) {
            ArrayList<DRItem> l = new ArrayList<DRItem>();
            for (String s : wiki.exists(wiki.getTemplatesOnPage(this.getTitle()), true)) {
                if (!s.startsWith("Commons:Deletion requests/")) continue;
                l.add(new DRItem(s));
            }
            return l.toArray(new DRItem[0]);
        }

        /* synthetic ProcLog(String string, ProcLog procLog) {
            ProcLog procLog2;
            procLog2(string);
        } */
    }

}

