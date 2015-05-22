/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.Credentials;
import fbot.lib.core.ImageInfo;
import fbot.lib.core.Request;
import fbot.lib.core.W;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.URL;
import javax.imageio.ImageIO;

public class FTask {
    private FTask() {
    }

    private static byte[] getBytes(String url, CookieManager cookiejar) {
        try {
            int c;
            BufferedInputStream in = new BufferedInputStream(Request.getInputStream(new URL(url), cookiejar));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            in.close();
            return out.toByteArray();
        }
        catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean downloadFile(String title, String localpath, W wiki) {
        return FTask.downloadFile(title, localpath, wiki, -1, -1);
    }

    public static boolean downloadFile(String title, String localpath, W wiki, int height, int width) {
        String url;
        ImageInfo x = wiki.getImageInfo(title, height, width);
        if (x.getThumbURL() != null) {
            url = x.getThumbURL();
        } else if (x.getURL() != null) {
            url = x.getURL();
        } else {
            return false;
        }
        try {
            FileOutputStream fos = new FileOutputStream(localpath);
            fos.write(FTask.getBytes(url, wiki.settings.cookiejar));
            fos.close();
        }
        catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static BufferedImage downloadFile(String title, W wiki) throws IOException {
        return FTask.downloadFile(title, wiki, -1, -1);
    }

    public static BufferedImage downloadFile(String title, W wiki, int height, int width) throws IOException {
        String url;
        ImageInfo x = wiki.getImageInfo(title, height, width);
        if (x.getThumbURL() != null) {
            url = x.getThumbURL();
        } else if (x.getURL() != null) {
            url = x.getURL();
        } else {
            return null;
        }
        return ImageIO.read(new ByteArrayInputStream(FTask.getBytes(url, wiki.settings.cookiejar)));
    }
}

