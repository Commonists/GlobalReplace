/*
 * Decompiled with CFR 0_101.
 */
package fbot.ft;

import fbot.lib.commons.WikiGen;
import fbot.lib.core.WMFWiki;
import fbot.lib.mbot.MAction;
import fbot.lib.mbot.WAction;
import fbot.lib.util.FError;
import fbot.lib.util.ReadFile;
import fbot.lib.util.FFile;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class Up {
    private static final String base = "=={{int:filedesc}}==\n{{Information\n|Description=%s\n|Source={{own}}\n|Date=%s\n|Author=~~~\n|Permission=\n|other_versions=\n}}\n\n=={{int:license-header}}==\n{{self|GFDL|cc-by-sa-3.0,2.5,2.0,1.0}}\n\n[[Category:%s]]\n[[Category:Files by Fastily]]";
    private static final SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String titledate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    private static final HashMap<String, Integer> tracker = new HashMap();

    public static void main(String[] args) throws InterruptedException {
        args = new String[]{"/Users/XXX/Desktop/Pacific Beach"};
        ArrayList<UploadItem> l = new ArrayList<UploadItem>();
        for (FFile wf : Up.parseArgs(args)) {
            l.add(Up.genUI(wf));
        }
        String[] fails = MAction.convertToString(WikiGen.genM("Fastily", 1).start(l.toArray(new UploadItem[0])));
        if (fails.length > 0) {
            new fbot.lib.util.WriteFile(new File("fails.txt"), fails);
        }
    }

    private static UploadItem genUI(FFile f) {
        String cat;
        String desc;
        String date = ddf.format(new Date(f.getFile().lastModified()));
        String parent = f.getParent(false);
        if (!parent.contains((CharSequence)"---")) {
            desc = parent;
            cat = parent;
        } else {
            String[] temp = parent.split("\\-\\-\\-");
            if (temp.length > 2) {
                FError.errAndExit(String.format("'%s' is not a valid title", parent));
            }
            cat = temp[0].trim();
            desc = temp[1].trim();
        }
        return new UploadItem(f, Up.genTitle(cat, f), String.format("=={{int:filedesc}}==\n{{Information\n|Description=%s\n|Source={{own}}\n|Date=%s\n|Author=~~~\n|Permission=\n|other_versions=\n}}\n\n=={{int:license-header}}==\n{{self|GFDL|cc-by-sa-3.0,2.5,2.0,1.0}}\n\n[[Category:%s]]\n[[Category:Files by Fastily]]", desc, date, cat));
    }

    private static String genTitle(String cat, FFile f) {
        int i = 1;
        if (tracker.containsKey(cat)) {
            i = tracker.get(cat) + 1;
            tracker.put(cat, new Integer(i));
        } else {
            tracker.put(cat, new Integer(i));
        }
        return String.format("%s %d %s.%s", cat, i, titledate, f.getExtension(false).toLowerCase());
    }

    private static FFile[] parseArgs(String[] args) {
        ArrayList<FFile> l = new ArrayList<FFile>();
        for (String s : args) {
            FFile w = new FFile(s);
            if (w.isDir()) {
                l.addAll(Arrays.asList(w.listFilesR(true)));
                continue;
            }
            if (!w.getExtension(false).matches("(?i)(txt)")) continue;
            l.clear();
            for (String x : new ReadFile(w.getFile()).getList()) {
                l.add(new FFile(x));
            }
            break;
        }
        return l.toArray(new FFile[0]);
    }

    private static class UploadItem
    extends WAction {
        private FFile f;
        private String uploadTo;

        private UploadItem(FFile f, String title, String text) {
            super(f.getPath(), text, null);
            this.uploadTo = title;
            this.f = f;
        }

        @Override
        public boolean doJob(WMFWiki wiki) {
            return wiki.upload(this.f.getFile(), this.uploadTo, this.text, " ");
        }

        /* synthetic UploadItem(WikiFile wikiFile, String string, String string2, UploadItem uploadItem) {
            UploadItem uploadItem2;
            uploadItem2(wikiFile, string, string2);
        } */
    }

}

