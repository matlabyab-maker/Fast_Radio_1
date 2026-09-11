package com.fast.radio;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Global radio search inspired by the useful search/filter behaviour of radio apps.
 *  It uses Radio Browser's public API rather than copying another app's proprietary code.
 */
public class RadioSearchEngine {
    private static final String API = "https://all.api.radio-browser.info/json/stations/search";
    private final Handler main = new Handler(Looper.getMainLooper());

    public interface Callback { void onResult(List<RadioStation> stations); void onError(String message); }

    public void search(final String name, final String countryCode, final String language,
                       final String tag, final int minBitrate, final int maxBitrate,
                       final boolean httpsOnly, final Callback callback) {
        new Thread(() -> {
            HttpURLConnection c = null;
            try {
                StringBuilder q = new StringBuilder(API).append("?hidebroken=true&limit=100&order=votes&reverse=true");
                if (!empty(name)) q.append("&name=").append(enc(name));
                if (!empty(countryCode)) q.append("&countrycode=").append(enc(countryCode));
                if (!empty(language)) q.append("&language=").append(enc(language));
                if (!empty(tag)) q.append("&tag=").append(enc(tag));
                if (minBitrate > 0) q.append("&bitrateMin=").append(minBitrate);
                if (maxBitrate > 0) q.append("&bitrateMax=").append(maxBitrate);
                if (httpsOnly) q.append("&is_https=true");

                URL u = new URL(q.toString());
                c = (HttpURLConnection) u.openConnection();
                c.setConnectTimeout(12000);
                c.setReadTimeout(15000);
                c.setRequestProperty("User-Agent", "Fast Radio/1.0");
                c.setRequestProperty("Accept", "application/json");
                int code = c.getResponseCode();
                if (code < 200 || code >= 300) throw new Exception("HTTP " + code);
                String json = read(c.getInputStream());
                JSONArray a = new JSONArray(json);
                List<RadioStation> out = new ArrayList<>();
                for (int i = 0; i < a.length(); i++) {
                    JSONObject o = a.optJSONObject(i);
                    if (o == null) continue;
                    String url = o.optString("url_resolved", o.optString("url", ""));
                    String n = o.optString("name", "");
                    if (url.length() == 0 || n.length() == 0) continue;
                    out.add(new RadioStation(n, url, o.optString("country", ""),
                            o.optString("language", ""), o.optString("tags", ""),
                            o.optInt("bitrate", 0), o.optString("codec", ""),
                            o.optString("homepage", "")));
                }
                main.post(() -> callback.onResult(out));
            } catch (Exception e) {
                main.post(() -> callback.onError(e.getMessage() == null ? "Search failed" : e.getMessage()));
            } finally { if (c != null) c.disconnect(); }
        }).start();
    }

    private static boolean empty(String s) { return s == null || s.trim().isEmpty(); }
    private static String enc(String s) throws Exception { return URLEncoder.encode(s.trim(), "UTF-8"); }
    private static String read(InputStream in) throws Exception {
        BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder b = new StringBuilder(); String line;
        while ((line = r.readLine()) != null) b.append(line);
        r.close(); return b.toString();
    }
}
