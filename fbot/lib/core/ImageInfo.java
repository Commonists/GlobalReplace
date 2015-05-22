/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.auxi.JSONParse;
import org.json.JSONObject;

public class ImageInfo {
    private int size;
    private int width;
    private int height;
    private String url;
    private String thumburl;

    protected ImageInfo(JSONObject jo) {
        this.size = JSONParse.getIntR(jo, "size");
        this.width = JSONParse.getIntR(jo, "width");
        this.height = JSONParse.getIntR(jo, "height");
        this.url = JSONParse.getStringR(jo, "url");
        this.thumburl = JSONParse.getStringR(jo, "thumburl");
    }

    public int getSize() {
        return this.size;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getURL() {
        return this.url;
    }

    public String getThumbURL() {
        return this.thumburl;
    }

    public String toString() {
        return String.format("----%nSize: %d%nWidth: %d%nHeight %d%nURL: %s%nThumbURL: %s%n----%n", this.size, this.width, this.height, this.url, this.thumburl);
    }
}

