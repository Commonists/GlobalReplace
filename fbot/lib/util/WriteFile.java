/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.util;

import fbot.lib.util.FString;
import fbot.lib.util.FSystem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

public class WriteFile {
    private File f;
    private String text;
    private boolean overwrite;
    private boolean writebyte = false;
    private int b;

    public WriteFile(File f, String text) {
        this(f, text, true);
    }

    public WriteFile(File f, String[] list) {
        this(f, FString.listCombo(list));
    }

    public WriteFile(File f, String text, boolean overwrite) {
        this.f = f;
        this.text = text;
        this.overwrite = overwrite;
    }

    public WriteFile(String f, String text, boolean overwrite) {
        this(new File(f), text, overwrite);
    }

    public WriteFile(File f, int b) {
        this(f, null, false);
        this.b = b;
        this.writebyte = true;
    }

    public void write() throws IOException {
        if (this.writebyte) {
            if (!(this.f.exists() && this.b > 0)) {
                return;
            }
            FileOutputStream out = new FileOutputStream(this.f, true);
            out.write(this.b); // TODO int or byte?
            out.close();
        } else if (this.overwrite) {
            PrintStream p = new PrintStream(this.f, FSystem.getDefaultCharset());
            p.print(this.text);
            p.close();
        } else {
            BufferedWriter out = new BufferedWriter(new FileWriter(this.f, this.f.exists()));
            out.write(this.text);
            out.close();
        }
    }
}

