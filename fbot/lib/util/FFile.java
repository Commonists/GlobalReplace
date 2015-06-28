/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class FFile {
    private static final String ALLOWED_FILE_TYPES = "(?i).+?\\.(png|gif|jpg|jpeg|xcf|mid|ogg|ogv|oga|svg|djvu|tiff|tif|pdf|webm|flac|wav)";
    private boolean isDirectory = false;
    private boolean isUploadable = false;
    private boolean exists = true;
    private File f;
    private String name;

    public FFile(File f) {
        this.f = f;
        this.name = f.getName();
        if (f.isDirectory()) {
            this.isDirectory = true;
        } else if (f.isFile() && FFile.hasAllowedFileExtension(this.name)) {
            this.isUploadable = true;
        } else {
            this.exists = false;
        }
    }

    public FFile(String s) {
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
        return this.name
                .substring(this.name.lastIndexOf(46) + (useDot ? 0 : 1));
    }

    public String getName(boolean withExt) {
        if (!withExt) {
            return this.name.contains((CharSequence) ".") ? this.name
                    .substring(0, this.name.lastIndexOf(46)) : this.name;
        }
        return this.name;
    }

    public File getFile() {
        return this.f;
    }

    public FFile[] listFiles(boolean canUploadOnly) {
        if (!this.isDirectory) {
            return null;
        }
        ArrayList<FFile> fl = new ArrayList<FFile>();
        if (canUploadOnly) {
            for (File x : this.f.listFiles()) {
                if (!x.isFile() || !FFile.hasAllowedFileExtension(x.getName()))
                    continue;
                fl.add(new FFile(x));
            }
            return fl.toArray(new FFile[0]);
        } else {
            for (File x : this.f.listFiles()) {
                if (!x.isFile())
                    continue;
                fl.add(new FFile(x));
            }
        }
        return fl.toArray(new FFile[0]);
    }

    public FFile[] listDirs() {
        if (!this.isDirectory) {
            return null;
        }
        ArrayList<FFile> wfl = new ArrayList<FFile>();
        for (File x : this.f.listFiles()) {
            if (!x.isDirectory())
                continue;
            wfl.add(new FFile(x));
        }
        return wfl.toArray(new FFile[0]);
    }

    public FFile[] listFilesR(boolean canUploadOnly) {
        if (!this.isDirectory) {
            return null;
        }
        ArrayList<FFile> wfl = new ArrayList<FFile>();
        wfl.addAll(Arrays.asList(this.listFiles(canUploadOnly)));
        for (FFile dir : this.listDirs()) {
            wfl.addAll(Arrays.asList(dir.listFilesR(canUploadOnly)));
        }
        return wfl.toArray(new FFile[0]);
    }

    public FFile[] listDirsR() {
        if (!this.isDirectory) {
            return null;
        }
        HashSet<FFile> dl = new HashSet<FFile>();
        List<FFile> dirs = Arrays.asList(this.listDirs());
        Arrays.asList(this.listDirs());
        for (FFile d : dirs) {
            dl.addAll(Arrays.asList(d.listDirsR()));
        }
        dl.addAll(dirs);
        return dl.toArray(new FFile[0]);
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
        } catch (Throwable e) {
            return this.f.getAbsolutePath();
        }
    }

    public String getParent(boolean fullpath) {
        return fullpath ? this.getPath() : this.f.getParentFile().getName();
    }

    public String toString() {
        return this.getPath();
    }

    /**
     * Check if the title ends with any of the allowed file types
     * 
     * @param title
     *            the title to check
     * @return if the extension is an allowed one
     */
    public static boolean hasAllowedFileExtension(String title) {
        return title.matches(ALLOWED_FILE_TYPES);
    }

    public static FFile[] convertTo(File... files) {
        ArrayList<FFile> wfl = new ArrayList<FFile>();
        for (File f : files) {
            wfl.add(new FFile(f));
        }
        return wfl.toArray(new FFile[0]);
    }

    public static File[] convertFrom(FFile... files) {
        ArrayList<File> fl = new ArrayList<File>();
        for (FFile wf : files) {
            fl.add(wf.getFile());
        }
        return fl.toArray(new File[0]);
    }
}
