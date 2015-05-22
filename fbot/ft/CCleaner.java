/*
 * Decompiled with CFR 0_101.
 */
package fbot.ft;

import fbot.ft.DRArchive;
import fbot.lib.commons.Commons;
import fbot.lib.core.W;
import fbot.lib.mbot.MBot;
import fbot.lib.mbot.WAction;
import fbot.lib.util.FCLI;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class CCleaner {
    public static void main(String[] args) {
        CommandLine l = CCleaner.parseArgs(args);
        if (l.hasOption('p')) {
            Commons.nukeLinksOnPage(l.getOptionValue('p'), l.getOptionValue('r'), "File");
        } else if (l.hasOption("dr")) {
            Commons.drDel(l.getOptionValue("dr"));
        } else if (l.hasOption('u')) {
            Commons.nukeUploads(l.getOptionValue('u'), l.getOptionValue('r'));
        } else if (l.hasOption('c')) {
            Commons.categoryNuke(l.getOptionValue('c'), l.getOptionValue('r'), false, new String[0]);
        } else if (l.hasOption('t')) {
            CCleaner.talkPageClear();
        } else if (l.hasOption('o')) {
            Commons.clearOSD(l.getOptionValue('r'), new String[0]);
        } else {
            Commons.categoryNuke("Copyright violations", "[[Commons:Licensing|Copyright violation]]: If you are the copyright holder/author and/or have authorization to upload the file, email [[COM:OTRS]]", false, "File");
            Commons.emptyCatDel(Commons.com.getCategoryMembers("Other speedy deletions", "Category"));
            Commons.emptyCatDel(Commons.com.getCategoryMembers("Non-media deletion requests", "Category"));
            Commons.nukeEmptyFiles(Commons.com.getCategoryMembers("Other speedy deletions", "File"));
            if (l.hasOption('d')) {
                CCleaner.unknownClear();
            }
            if (l.hasOption('a')) {
                DRArchive.main(new String[0]);
            } else if (l.hasOption("ac")) {
                DRArchive.main(new String[]{"-c"});
            }
        }
    }

    private static CommandLine parseArgs(String[] args) {
        Options ol = new Options();
        OptionGroup og = new OptionGroup();
        og.addOption(FCLI.makeArgOption("dr", "Delete all files linked in a DR", "DR"));
        og.addOption(FCLI.makeArgOption("p", "Set mode to delete all files linked on a page", "title"));
        og.addOption(FCLI.makeArgOption("u", "Set mode to delete all uploads by a user", "username"));
        og.addOption(FCLI.makeArgOption("c", "Set mode to delete all category members", "category"));
        og.addOption(new Option("o", false, "Delete all members of a Other Speedy Deletions"));
        og.addOption(new Option("t", false, "Clears orphaned talk pages from DBR"));
        og.addOption(new Option("a", false, "Archive DRs ready for archiving"));
        og.addOption(new Option("ac", false, "Close all Singleton DRs"));
        ol.addOptionGroup(og);
        ol.addOption(FCLI.makeArgOption("r", "Reason param, for use with options that require a reason", "reason"));
        ol.addOption("help", false, "Print this help message and exit");
        ol.addOption("d", false, "Deletes everything we can in Category:Unknown");
        return FCLI.gnuParse(ol, args, "CCleaner [-dr|-t|[-p <title>|-u <user>|-c <cat>] -r <reason>]] [-d] [-a|-ac]");
    }

    private static String[] talkPageClear() {
        ArrayList<String> l = new ArrayList<String>();
        Scanner m = new Scanner(Commons.com.getPageText("Commons:Database reports/Orphaned talk pages"));
        while (m.hasNextLine()) {
            String ln = m.nextLine();
            if (!ln.contains((CharSequence)"{{plnr")) continue;
            l.add(ln.substring(ln.indexOf("=") + 1, ln.indexOf("}}")));
        }
        m.close();
        return Commons.nuke("Orphaned talk page", l.toArray(new String[0]));
    }

    private static String[] unknownClear() {
        Commons.com.nullEdit("User:FSV/UC");
        ArrayList<String> catlist = new ArrayList<String>();
        ArrayList<MBot.DeleteItem> l = new ArrayList<MBot.DeleteItem>();
        for (String c : Commons.com.getValidLinksOnPage("User:FSV/UC", new String[0])) {
            catlist.add(c);
            String r = c.contains((CharSequence)"permission") ? String.format("[[COM:OTRS|No permission]] since %s", c.substring(c.indexOf("as of") + 6)) : (c.contains((CharSequence)"license") ? String.format("No license since %s", c.substring(c.indexOf("as of") + 6)) : String.format("No source since %s", c.substring(c.indexOf("as of") + 6)));
            for (String s : Commons.com.getCategoryMembers(c, "File")) {
                l.add(new MBot.DeleteItem(s, r));
            }
        }
        Commons.doAction("Fastily", l.toArray(new WAction[0]));
        return Commons.emptyCatDel(catlist.toArray(new String[0]));
    }
}

