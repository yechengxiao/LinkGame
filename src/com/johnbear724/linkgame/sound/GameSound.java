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
    
    public static final int REFRESH = 0x1;
    public static final int CLICK = 0x2;
    public static final int COMB_1 = 0x31;
    public static final int COMB_3 = 0x33;
    public static final int COMB_5 = 0x35;
    public static final int TIME_UP = 0x4;
    public static final int WIN = 0x5;
    public static final int COUNT = 0x6;
    public static final int GOOD = 0x7;
    public static final int NICE = 0x8;
    public static final int COOL = 0x9;
    public static final int CRAZY = 0x10;
    
    
    public GameSound(Context context) {
        // TODO Auto-generated constructor stub
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mediaPlay = MediaPlayer.create(context, R.raw.play);
        mediaPlay.setLooping(true);
        
        soundMap.put(REFRESH, soundPool.load(context, R.raw.refresh, 1));
        soundMap.put(CLICK, soundPool.load(context, R.raw.click1, 1));
        soundMap.put(COMB_1, soundPool.load(context, R.raw.a_combo1, 1));
        soundMap.put(COMB_3, soundPool.load(context, R.raw.a_combo3, 1));
        soundMap.put(COMB_5, soundPool.load(context, R.raw.a_combo5, 1));
        soundMap.put(TIME_UP, soundPool.load(context, R.raw.timeup, 1));
        soundMap.put(WIN, soundPool.load(context, R.raw.rankchange, 1));
        soundMap.put(COUNT, soundPool.load(context, R.raw.count, 1));
        soundMap.put(GOOD, soundPool.load(context, R.raw.good, 1));
        soundMap.put(NICE, soundPool.load(context, R.raw.nice, 1));
        soundMap.put(COOL, soundPool.load(context, R.raw.cool, 1));
        soundMap.put(CRAZY, soundPool.load(context, R.raw.crazy, 1));
    }
    
    public int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
        return soundPool.play(soundMap.get(soundID), leftVolume, rightVolume, priority, loop, rate);
    }
    
    public MediaPlayer getPlayer() {
        return mediaPlay;
    }
    
    public void stop(int streamID) {
        soundPool.stop(streamID);
    }
    
    public SoundPool getSoundPool() {
        return soundPool;
    }
    
    public void autoPause() {
        if(mediaPlay.isPlaying()) {
            mediaPlay.pause();
        }
        soundPool.autoPause();
    }
    
    public void autoResume(boolean isStart) {
        if(isStart) {
            mediaPlay.start();
        }
        soundPool.autoResume();
    }
}
