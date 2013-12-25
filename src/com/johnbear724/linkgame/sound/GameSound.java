package com.johnbear724.linkgame.sound;

import com.johnbear724.linkgame.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.SparseIntArray;

public class GameSound {
    
    private SparseIntArray soundMap = new SparseIntArray();
    private SoundPool soundPool;
    private MediaPlayer mediaPlay;
    
    public static final int REFRESH = 1;
    public static final int CLICK = 2;
    public static final int COMB_1 = 3;
    public static final int TIME_UP = 4;
    public static final int WIN = 5;
    
    public GameSound(Context context) {
        // TODO Auto-generated constructor stub
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mediaPlay = MediaPlayer.create(context, R.raw.play);
        mediaPlay.setLooping(true);
        
        soundMap.put(REFRESH, soundPool.load(context, R.raw.refresh, 1));
        soundMap.put(CLICK, soundPool.load(context, R.raw.click1, 1));
        soundMap.put(COMB_1, soundPool.load(context, R.raw.a_combo1, 1));
        soundMap.put(TIME_UP, soundPool.load(context, R.raw.timeup, 1));
        soundMap.put(WIN, soundPool.load(context, R.raw.rankchange, 1));
    }
    
    public void play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
        soundPool.play(soundMap.get(soundID), leftVolume, rightVolume, priority, loop, rate);
    }
    
    public MediaPlayer getPlayer() {
        return mediaPlay;
    }
}
