/*
 * Decompiled with CFR 0_101.
 */
package fbot.lib.core;

import fbot.lib.core.Reply;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Request {
    private static final int connectTimeout = 60000;
    private static final int readTimeout = 360000;
    protected static final String urlenc = "application/x-www-form-urlencoded";

    private static void setCookies(URLConnection c, CookieManager cookiejar) {
        String cookie = "";
        for (HttpCookie hc : cookiejar.getCookieStore().getCookies()) {
            cookie = String.valueOf(cookie) + hc.toString() + ";";
        }
        c.setRequestProperty("Cookie", cookie);
        c.setRequestProperty("User-Agent", "powerfastilytoy");
    }

    private static void grabCookies(URLConnection u, CookieManager cookiejar) {
        try {
            cookiejar.put(u.getURL().toURI(), u.getHeaderFields());
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected static Reply post(URL url, String text, CookieManager cookiejar, String contenttype) throws IOException {
        URLConnection c = url.openConnection();
        c.setConnectTimeout(60000);
        c.setReadTimeout(360000);
        Request.setCookies(c, cookiejar);
        if (contenttype != null) {
            c.setRequestProperty("Content-Type", contenttype);
        }
        c.setDoOutput(true);
        c.connect();
        OutputStreamWriter out = new OutputStreamWriter(c.getOutputStream(), "UTF-8");
        out.write(text);
        out.close();
        Request.grabCookies(c, cookiejar);
        return new Reply(c.getInputStream());
    }

    protected static Reply chunkPost(URL url, Map<String, ?> params, CookieManager cookiejar) throws IOException {
        String boundary = "-----Boundary-----";
        URLConnection c = url.openConnection();
        c.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        c.setConnectTimeout(60000);
        c.setReadTimeout(360000);
        Request.setCookies(c, cookiejar);
        c.setDoOutput(true);
        c.connect();
        boundary = "--" + boundary + "\r\n";
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);
        out.writeBytes(boundary);
        for (Map.Entry entry : params.entrySet()) {
            String name = (String) entry.getKey();
            Object value = entry.getValue();
            out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n");
            if (value instanceof String) {
                out.writeBytes("Content-Type: text/plain; charset=UTF-8\r\n\r\n");
                out.write(((String)value).getBytes("UTF-8"));
            } else if (value instanceof byte[]) {
                out.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
                out.write((byte[])value);
            } else {
                throw new UnsupportedOperationException("Unrecognized data type");
            }
            out.writeBytes("\r\n" + boundary);
        }
        out.writeBytes("--\r\n");
        out.close();
        OutputStream uout = c.getOutputStream();
        uout.write(bout.toByteArray());
        uout.close();
        out.close();
        Request.grabCookies(c, cookiejar);
        return new Reply(c.getInputStream());
    }

    protected static Reply get(URL url, CookieManager cookiejar) throws IOException {
        return new Reply(Request.getInputStream(url, cookiejar));
    }

    protected static InputStream getInputStream(URL url, CookieManager cookiejar) throws IOException {
        URLConnection c = url.openConnection();
        c.setConnectTimeout(60000);
        c.setReadTimeout(360000);
        if (cookiejar != null) {
            Request.setCookies(c, cookiejar);
        }
        c.connect();
        return c.getInputStream();
    }
}

