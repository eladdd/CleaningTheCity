package com.example.cleaningthecity;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import java.io.IOException;


public class MusicButton extends AppCompatButton {
    MediaPlayer mediaPlayer;
    private boolean play = true;

    public MusicButton(Context context) {
        super(context);
        mediaPlayer = MediaPlayer.create(context, R.raw.gamesound);
    }

    public MusicButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mediaPlayer = MediaPlayer.create(context, R.raw.gamesound);
    }

    public MusicButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mediaPlayer = MediaPlayer.create(context, R.raw.gamesound);
    }

    protected void destroySound()
    {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()== MotionEvent.ACTION_DOWN) {
            performClick();
            return true;
        }

    return false;
}

    @Override
    public boolean performClick() {
        // android studio suggest to override this to remind think about the blind
        // or visually impaired people who may be using app
        super.performClick();
        if(play)
        {
            play = false;
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
            setBackgroundResource(R.drawable.music);
        }
        else
        {
            play = true;
            try {
                mediaPlayer.stop();
                mediaPlayer.prepare(); // to allow re play the music
            } catch (IOException e) {
                e.printStackTrace();
            }
            setBackgroundResource(R.drawable.musicoff);
        }
        touchAlert();
        return true;
    }
    private void touchAlert() {
        // show toast msg when user clicked the music button
        String msg = getResources().getString(R.string.sound_click);
        Toast.makeText(getContext(), msg , Toast.LENGTH_SHORT).show();
    }
}

