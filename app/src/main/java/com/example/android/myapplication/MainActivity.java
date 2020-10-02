package com.example.android.myapplication;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class MainActivity extends AppCompatActivity {

    // Instantiating the MediaPlayer class
    private MediaPlayer music;

    //handles the audio focus when playing a sound file.
    private AudioManager myAudioManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialising the music player object.
        music = MediaPlayer.create(getApplicationContext(), R.raw.song);

        //initializing the audio manager object.
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            //logic for the play functionality
            Button startButton = findViewById(R.id.start);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //requesting  the audio focus for playing the file.
                    int result = myAudioManager.requestAudioFocus(audioFocusChangeListener,
                            AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                    if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                        Log.i("musicplayer -start", "focus gained");
                        //checking for any media player already playing.
                        //clearing the player already if one exists.
                        if(music!= null && music.isPlaying()){
                            releaseMediaPlayer();
                        }
                        if(music!=null){
                            music.start();
                            Log.i("music player ", "playback started");
                            Toast.makeText(getBaseContext(), "started music player", Toast.LENGTH_SHORT).show();
                            //animation for the buttons
                            YoYo.with(Techniques.FadeIn)
                                    .duration(700)
                                    .repeat(0)
                                    .playOn(findViewById(R.id.start));

                            //setting to display message after the media player is done playing the audio file.
                           /* music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    releaseMediaPlayer();
                                    Log.i("music player", "music playback completed stopping player and releasing res.");
                                    Toast.makeText(getApplicationContext(), "Released  media player resources", Toast.LENGTH_SHORT).show();
                                }
                            });//end of onCompletionListener*/
                        }else {
                            Log.i("musicplayer", "OOPS !!!!!  music player object is a null ");
                        }
                    }
                }
            });//end of onClickListener for start Button.

            //logic for the pause functionality
            Button pauseButton = findViewById(R.id.pause);
            pauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(music!= null && music.isPlaying()){
                        music.pause();
                        Log.i("music Player", "called the pause button code. ");
                        YoYo.with(Techniques.FadeIn)
                                .duration(700)
                                .repeat(0)
                                .playOn(findViewById(R.id.pause));
                        myAudioManager.abandonAudioFocus(audioFocusChangeListener);
                    }else  {
                        Toast.makeText(getApplicationContext(),"no song is playing", Toast.LENGTH_SHORT).show();
                    }
                }
            });//end of onClickListener for pause Button.

            //logic for the stop functionality
            Button stopButton = findViewById(R.id.stop);

            //using a OnClickListener to stop the music player and release its resources.
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (music != null && music.isPlaying()) { //If music is playing already
                        music.stop();//Stop playing the music
                        YoYo.with(Techniques.FadeIn)
                                .duration(700)
                                .repeat(0)
                                .playOn(findViewById(R.id.stop));
                       myAudioManager.abandonAudioFocus(audioFocusChangeListener);
                        music = MediaPlayer.create(getApplicationContext(), R.raw.song);
                        Log.i("musicPlayer", "audio focus abandoned");
                        Log.i("musicPlayer", "called the stop player code inside the stop button code.");
                    }
                }
            });// end of onCLickListener for the stop button.
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("musicPlayer", "app activity is destroyed.");
        releaseMediaPlayer();
        Toast.makeText(getApplicationContext(), "music player killed", Toast.LENGTH_SHORT).show();
    }

    public void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (music != null) {
            // Regardless of the current state of the media player, release its resources.
            // because we no longer need it.
            music.release();

            // Set the media player back to null. For our code, we've decided that.
            // setting the media player to null is an easy way to tell that the media player.
            // is not configured to play an audio file at the moment.
            music = null;
            myAudioManager.abandonAudioFocus(audioFocusChangeListener);
            Log.i("music player","the music player focus is abandoned");
        }
    }

    /**
     * This listener gets triggered whenever the audio focus changes.
     * (i.e., we gain or lose audio focus because of another app or device).
     */
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
                if(music!= null && music.isPlaying()){
                    music.pause();
                    Log.i("music player", "pausing audio because of loss of focus");
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                music.start();
                Log.i("music player", "starting audio because of permanent gain of focus");
            }else if (focusChange == AudioManager.AUDIOFOCUS_LOSS){
                if(music!= null && music.isPlaying()){
                    music.stop();
                    Log.i("music player", "stopping audio because of permanent loss of focus");
                    releaseMediaPlayer();
                }
            }
        }
    };//end of OnAudioFocusChangeListener
}