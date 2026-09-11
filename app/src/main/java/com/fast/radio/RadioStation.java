package com.fast.radio;

public class RadioStation {
    public final String name;
    public final String url;
    public final String country;
    public final String language;
    public final String tags;
    public final int bitrate;
    public final String codec;
    public final String homepage;

    public RadioStation(String name, String url, String country, String language,
                        String tags, int bitrate, String codec, String homepage) {
        this.name = name == null ? "" : name;
        this.url = url == null ? "" : url;
        this.country = country == null ? "" : country;
        this.language = language == null ? "" : language;
        this.tags = tags == null ? "" : tags;
        this.bitrate = bitrate;
        this.codec = codec == null ? "" : codec;
        this.homepage = homepage == null ? "" : homepage;
    }

    @Override public String toString() { return name; }
}
