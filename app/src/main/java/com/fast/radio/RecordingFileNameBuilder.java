package com.fast.radio;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Builds Fast Radio recording filenames. No embedded tags are written. */
public final class RecordingFileNameBuilder {
    private RecordingFileNameBuilder() {}

    public static String build(String country, String station, String genre,
                               String countryCode, long timeMillis) {
        String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
                .format(new Date(timeMillis));
        return clean(country) + "_" + clean(station) + "_" + clean(genre) + "_"
                + clean(countryCode) + "_" + date + ".amr";
    }

    private static String clean(String value) {
        String s = value == null ? "Unknown" : value.trim();
        s = s.replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", "_");
        return s.isEmpty() ? "Unknown" : s;
    }
}
