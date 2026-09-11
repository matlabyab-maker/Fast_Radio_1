package com.fast.radio;

import android.content.Context;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

public class RadioPlayer {
    private final ExoPlayer player;

    public RadioPlayer(Context context) {
        player = new ExoPlayer.Builder(context).build();
    }

    public void play(String url) {
        player.setMediaItem(MediaItem.fromUri(url));
        player.prepare();
        player.play();
    }

    public void pause() { player.pause(); }
    public void stop() { player.stop(); }
    public boolean isPlaying() { return player.isPlaying(); }
    public void release() { player.release(); }
}
