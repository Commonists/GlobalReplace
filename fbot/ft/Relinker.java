/*
 * Decompiled with CFR 0_101.
 */
package fbot.ft;

import fbot.lib.commons.WikiGen;
import fbot.lib.core.Namespace;
import fbot.lib.core.Tools;
import fbot.lib.core.W;
import fbot.lib.core.auxi.Logger;
import fbot.lib.core.auxi.Tuple;
import fbot.lib.util.FError;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Relinker {
    public static void main(String[] args) {
        for (String arg : args) {
            Relinker.process(Relinker.makeList(arg));
        }
    }

    private static String getTableText(String title) {
        String html = Relinker.getHTMLOf(String.format("https://tools.wmflabs.org/delinker/index.php?image=%s&status=ok&max=500", Tools.enc(Namespace.nss(title))));
        Matcher m = Pattern.compile("(?si)class\\=\"table table\\-hover.*?\\</table\\>", 34).matcher((CharSequence)html);
        if (m.find()) {
            return html.substring(m.start(), m.end());
        }
        FError.errAndExit("Error: We didn't find a log for " + title);
        return null;
    }

    private static ArrayList<Tuple<String, String>> makeList(String title) {
        ArrayList<Tuple<String, String>> l = new ArrayList<Tuple<String, String>>();
        String text = Relinker.getTableText(title);
        Matcher m = Pattern.compile("\\<tr\\>.*?\\</tr\\>", 34).matcher((CharSequence)text);
        while (m.find()) {
            String curr = text.substring(m.start(), m.end());
            if (curr.contains((CharSequence)"<b>Timestamp</b>")) continue;
            String[] cl = curr.split("\n");
            l.add(new Tuple<String, String>(Relinker.getAnchorArg(cl[4]), Relinker.getAnchorArg(cl[3])));
        }
        return l;
    }

    private static String getAnchorArg(String l) {
        return l.substring(l.indexOf("\">") + 2, l.indexOf("</a>"));
    }

    private static String getHTMLOf(String url) {
        try {
            String line;
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream(), "UTF-8"));
            String x = "";
            while ((line = in.readLine()) != null) {
                x = String.valueOf(x) + line + "\n";
            }
            in.close();
            return x.trim();
        }
        catch (Throwable e) {
            FError.errAndExit(e);
            return null;
        }
    }

    private static void process(ArrayList<Tuple<String, String>> l) {
        W wiki = WikiGen.generate("FSV");
        String last = null;
        for (Tuple<String, String> t : l) {
            if (!((String)t.y).equals(last)) {
                wiki.switchDomain((String)t.y);
                last = (String)t.y;
            }
            try {
                if (!wiki.getRevisions((String)t.x, 1, false)[0].getUser().contains((CharSequence)"CommonsDelinker")) continue;
                wiki.undo((String)t.x, "Reverting CommonsDelinker");
                continue;
            }
            catch (Throwable e) {
                System.out.println((String)t.x);
                Logger.warn(String.valueOf((String)t.x) + " doesn't seem to exist");
            }
        }
    }
}

