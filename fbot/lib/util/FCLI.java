/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.util;

import fbot.lib.util.FError;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class FCLI {
    public static Option makeArgOption(String title, String desc, String argname) {
        Option o = new Option(title, true, desc);
        o.setArgName(argname);
        return o;
    }

    public static /* varargs */ OptionGroup makeOptGroup(Option ... ol) {
        OptionGroup og = new OptionGroup();
        for (Option o : ol) {
            og.addOption(o);
        }
        return og;
    }

    public static CommandLine gnuParse(Options ol, String[] args, String help) {
        try {
            CommandLine l = new GnuParser().parse(ol, args);
            if (l.hasOption("help")) {
                HelpFormatter hf = new HelpFormatter();
                hf.setWidth(120);
                hf.printHelp(help, ol);
                System.exit(0);
            }
            return l;
        }
        catch (Throwable e) {
            FError.errAndExit(e);
            return null;
        }
    }
}

