/*
 * Decompiled with CFR 0_101.
 */
package fbot.ft;

import fbot.lib.commons.Commons;
import fbot.lib.commons.WikiGen;
import fbot.lib.core.W;
import fbot.lib.core.auxi.Logger;
import fbot.lib.mbot.MAction;
import fbot.lib.mbot.MBot;
import fbot.lib.mbot.WAction;
import fbot.lib.util.FCLI;
import fbot.lib.util.FString;
import fbot.lib.util.ReadFile;
import fbot.lib.util.WikiFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CC {
    public static final String utt = "Recreating [[bugzilla:36587]] (i.e. [[Special:UploadStash|upload stash]] bug) & collecting data to log.\n{{Warning|'''Test area only!  File may be non-free.''' This is just a test file and any license does not apply.}}\n[[Category:Fastily Test]]";
    private static boolean nd = false;
    private static int repeats;
    private static boolean nr;

    public static void main(String[] args) throws ParseException {
        CommandLine l = CC.parseArgs(args);
        if (l.hasOption('f')) {
            Commons.nukeFastilyTest(true);
        }
        nd = l.hasOption("nd") || l.hasOption("sd");
        repeats = Integer.parseInt(l.getOptionValue('r', "1"));
        nr = l.hasOption("nr");
        MAction[] ccwl = l.hasOption('t') ? CC.generateCCW(new ReadFile(l.getOptionValue('t')).getList()) : CC.generateCCW(l.getArgs());
        MAction[] ml = new MBot(WikiGen.generate("FSVI"), Integer.parseInt(l.getOptionValue('h', "1"))).start(ccwl);
        if (l.hasOption('m')) {
            Commons.com.edit("User:Fastily/A5", "Generated at ~~~~~\n\n" + FString.listCombo(MAction.convertToString(ml)), "Update report");
        }
    }

    private static /* varargs */ CCW[] generateCCW(String ... paths) {
        HashSet<WikiFile> sl = new HashSet<WikiFile>();
        for (String s : paths) {
            WikiFile t = new WikiFile(s);
            if (t.isDir()) {
                sl.addAll(Arrays.asList(t.listFilesR(!nr)));
                continue;
            }
            if (!t.canUp()) continue;
            sl.add(t);
        }
        if (sl.isEmpty()) {
            sl.addAll(Arrays.asList(new WikiFile(".").listFilesR(!nr)));
        }
        ArrayList<CCW> x = new ArrayList<CCW>();
        for (WikiFile f : sl) {
            x.add(new CCW(f));
        }
        return x.toArray(new CCW[0]);
    }

    private static CommandLine parseArgs(String[] args) throws ParseException {
        Options ol = new Options();
        ol.addOption("nd", false, "Surpress deletion after upload");
        ol.addOption("sd", false, "Alias of '-nd'");
        ol.addOption("f", false, "Nuke 'Category:Fastily Test' and exit.  Overrides other options");
        ol.addOption("m", false, "Make a note of failures on-wiki");
        ol.addOption("nr", false, "Turn off recursive file search");
        ol.addOption("help", false, "Print this help message and exit");
        ol.addOption(FCLI.makeArgOption("h", "Sets the number of threads of execution", "#threads"));
        ol.addOption(FCLI.makeArgOption("r", "Number of times to repeat in event of failure", "#retries"));
        ol.addOption(FCLI.makeArgOption("t", "Select files to upload from a text file", "<textfile>"));
        return FCLI.gnuParse(ol, args, "CC [-m] [-nr] [-help] [-h number] [-r retries] [-f] [-nd|-sd] [-t <textfile>|<files or directories>]");
    }

    private static class CCW
    extends WAction {
        private static W ft = WikiGen.generate("Fastily");
        private WikiFile f;

        protected CCW(WikiFile f) {
            super(f.getPath(), "Recreating [[bugzilla:36587]] (i.e. [[Special:UploadStash|upload stash]] bug) & collecting data to log.\n{{Warning|'''Test area only!  File may be non-free.''' This is just a test file and any license does not apply.}}\n[[Category:Fastily Test]]", "");
            this.f = f;
        }

        @Override
        public boolean doJob(W wiki) {
            for (int i = 0; i < repeats; ++i) {
                String fn = "File:" + FString.generateRandomFileName(this.f);
                Logger.fyi(String.format("(%d/%d): Upload '%s' -> '%s'", i + 1, repeats, this.f, fn));
                if (!wiki.upload(this.f.getFile(), fn, this.text, " ")) continue;
                if (!nd) {
                    ft.delete(fn, "Uploader requested deletion of a recently uploaded and unused file");
                }
                return true;
            }
            return false;
        }
    }

}

