package com.fast.radio;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** Reads the public data endpoints referenced by Iran Radio 36.0.
 * The parser is intentionally tolerant because the remote JSON structure can change.
 */
public class IranRadioRepository {
    private static final String[] SOURCES = {
            "https://kurdin.s3.us-west-1.amazonaws.com/config/iran.json",
            "https://kurdin.s3.us-west-1.amazonaws.com/server/iran.json",
            "https://kurdin.s3.us-west-1.amazonaws.com/radio/edm/i_radio.json"
    };
    private final Handler main = new Handler(Looper.getMainLooper());
    public interface Callback { void onResult(List<RadioStation> stations); void onError(String message); }

    public void load(Callback cb) {
        new Thread(() -> {
            List<RadioStation> all = new ArrayList<>();
            StringBuilder errors = new StringBuilder();
            for (String source : SOURCES) {
                try {
                    String text = get(source);
                    Object root = new org.json.JSONTokener(text).nextValue();
                    collect(root, all);
                } catch (Exception e) { errors.append(source).append(": ").append(e.getMessage()).append("\n"); }
            }
            dedupe(all);
            if (all.isEmpty() && errors.length() > 0) {
                main.post(() -> cb.onError("Iran Radio sources unavailable"));
            } else main.post(() -> cb.onResult(all));
        }).start();
    }

    private void collect(Object v, List<RadioStation> out) {
        if (v instanceof JSONObject) {
            JSONObject o = (JSONObject)v;
            String name = first(o, "stationName","radioName","name","title","station_name");
            String url = first(o, "stationUrl","radioUrl","streamUrl","url","stream","urlLq","urlHq","station_url");
            if (!name.isEmpty() && looksLikeUrl(url)) {
                out.add(new RadioStation(name, url, first(o,"country","countryName"),
                        first(o,"language","lang"), first(o,"genre","genres","tags"),
                        o.optInt("bitrate",0), first(o,"codec","format"), first(o,"homepage","website")));
            }
            java.util.Iterator<String> it = o.keys();
            while (it.hasNext()) { String k = it.next(); collect(o.opt(k), out); }
        } else if (v instanceof JSONArray) {
            JSONArray a=(JSONArray)v; for(int i=0;i<a.length();i++) collect(a.opt(i),out);
        }
    }
    private static String first(JSONObject o, String... keys) {
        for(String k:keys){ String s=o.optString(k,""); if(!s.trim().isEmpty()) return s.trim(); }
        return "";
    }
    private static boolean looksLikeUrl(String s){ return s.startsWith("http://") || s.startsWith("https://"); }
    private static String get(String s)throws Exception{
        HttpURLConnection c=(HttpURLConnection)new URL(s).openConnection(); c.setConnectTimeout(10000); c.setReadTimeout(15000);
        c.setRequestProperty("User-Agent","Fast Radio/1.0");
        if(c.getResponseCode()<200||c.getResponseCode()>=300) throw new Exception("HTTP "+c.getResponseCode());
        BufferedReader r=new BufferedReader(new InputStreamReader(c.getInputStream(),"UTF-8")); StringBuilder b=new StringBuilder(); String l;
        while((l=r.readLine())!=null)b.append(l); r.close(); c.disconnect(); return b.toString();
    }
    private static void dedupe(List<RadioStation> a){
        java.util.HashSet<String> seen=new java.util.HashSet<>();
        java.util.Iterator<RadioStation> it=a.iterator();
        while(it.hasNext()){ RadioStation s=it.next(); String k=s.name.toLowerCase()+"|"+s.url; if(!seen.add(k)) it.remove(); }
    }
}
