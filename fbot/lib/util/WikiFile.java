/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class WikiFile {
    private boolean isDirectory = false;
    private boolean isUploadable = false;
    private boolean exists = true;
    private File f;
    private String name;

    public WikiFile(File f) {
        this.f = f;
        this.name = f.getName();
        if (f.isDirectory()) {
            this.isDirectory = true;
        } else if (f.isFile() && WikiFile.canUpload(this.name)) {
            this.isUploadable = true;
        } else {
            this.exists = false;
        }
    }

    public WikiFile(String s) {
        this(new File(s));
    }

    public String getExtension(boolean useDot) {
        if (this.isDirectory) {
            return null;
        }
        int i = this.name.lastIndexOf(46);
        if (i == -1) {
            return "";
        }
        return this.name.substring(this.name.lastIndexOf(46) + (useDot ? 0 : 1));
    }

    public String getName(boolean withExt) {
        if (!withExt) {
            return this.name.contains((CharSequence)".") ? this.name.substring(0, this.name.lastIndexOf(46)) : this.name;
        }
        return this.name;
    }

    public File getFile() {
        return this.f;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public WikiFile[] listFiles(boolean canUploadOnly) {
        if (!this.isDirectory) {
            return null;
        }
        ArrayList<WikiFile> fl = new ArrayList<WikiFile>();
        if (canUploadOnly) {
            for (File x : this.f.listFiles()) {
                if (!x.isFile() || !WikiFile.canUpload(x.getName())) continue;
                fl.add(new WikiFile(x));
            }
            return fl.toArray(new WikiFile[0]);
        } else {
            for (File x : this.f.listFiles()) {
                if (!x.isFile()) continue;
                fl.add(new WikiFile(x));
            }
        }
        return fl.toArray(new WikiFile[0]);
    }

    public WikiFile[] listDirs() {
        if (!this.isDirectory) {
            return null;
        }
        ArrayList<WikiFile> wfl = new ArrayList<WikiFile>();
        for (File x : this.f.listFiles()) {
            if (!x.isDirectory()) continue;
            wfl.add(new WikiFile(x));
        }
        return wfl.toArray(new WikiFile[0]);
    }

    public WikiFile[] listFilesR(boolean canUploadOnly) {
        if (!this.isDirectory) {
            return null;
        }
        ArrayList<WikiFile> wfl = new ArrayList<WikiFile>();
        wfl.addAll(Arrays.asList(this.listFiles(canUploadOnly)));
        for (WikiFile dir : this.listDirs()) {
            wfl.addAll(Arrays.asList(dir.listFilesR(canUploadOnly)));
        }
        return wfl.toArray(new WikiFile[0]);
    }

    public WikiFile[] listDirsR() {
        if (!this.isDirectory) {
            return null;
        }
        HashSet<WikiFile> dl = new HashSet<WikiFile>();
        List<WikiFile> dirs = Arrays.asList(this.listDirs());
        Arrays.asList(this.listDirs());
        for (WikiFile d : dirs) {
            dl.addAll(Arrays.asList(d.listDirsR()));
        }
        dl.addAll(dirs);
        return dl.toArray(new WikiFile[0]);
    }

    public boolean isDir() {
        return this.isDirectory;
    }

    public boolean canUp() {
        return this.isUploadable;
    }

    public boolean doesExist() {
        return this.exists;
    }

    public String getPath() {
        try {
            return this.f.getCanonicalPath();
        }
        catch (Throwable e) {
            return this.f.getAbsolutePath();
        }
    }

    public String getParent(boolean fullpath) {
        return fullpath ? this.getPath() : this.f.getParentFile().getName();
    }

    public String toString() {
        return this.getPath();
    }

    public static boolean canUpload(String title) {
        return title.matches("(?i).+?\\.(png|gif|jpg|jpeg|xcf|mid|ogg|ogv|oga|svg|djvu|tiff|tif|pdf|webm|flac|wav)");
    }

    public static /* varargs */ WikiFile[] convertTo(File ... files) {
        ArrayList<WikiFile> wfl = new ArrayList<WikiFile>();
        for (File f : files) {
            wfl.add(new WikiFile(f));
        }
        return wfl.toArray(new WikiFile[0]);
    }

    public static /* varargs */ File[] convertFrom(WikiFile ... files) {
        ArrayList<File> fl = new ArrayList<File>();
        for (WikiFile wf : files) {
            fl.add(wf.getFile());
        }
        return fl.toArray(new File[0]);
    }
}

