package com.agility.game.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicHandler {

    private Music[] soundtracks = new Music[6];
    private Music[] bossSoundtracks = new Music[2];
    private Music nowPlaying;

    public MusicHandler() {
        for (int i = 0; i < soundtracks.length; i++) {
            soundtracks[i] = Gdx.audio.newMusic(Gdx.files.internal("music/s"+i+".mp3"));
        }
        bossSoundtracks[0] = Gdx.audio.newMusic(Gdx.files.internal("music/boss.mp3"));
        bossSoundtracks[1] = Gdx.audio.newMusic(Gdx.files.internal("music/boss2.mp3"));
    }

    public void begin(int level) {
        if(level == 5) {
            // Boss
            nowPlaying = bossSoundtracks[0];
        }
        else {
            nowPlaying = soundtracks[level % soundtracks.length];
        }
        nowPlaying.setLooping(true);
        nowPlaying.play();
    }

    public void refresh() {
        nowPlaying.stop();
    }
}
