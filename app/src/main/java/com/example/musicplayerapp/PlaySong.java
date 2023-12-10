package com.example.musicplayerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {

    // OnDestroy Method override to stop music after back button
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop(); // to stop musicPlayer
        mediaPlayer.release(); // release the player
        updateSeek.interrupt(); // to stop the thread
    }
    // Creating Objects or Instances of req Classes
    TextView textView;
    ImageView play, previous, next;
    ArrayList<File> songs; // Getting Songs from intent
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;

    // To update seekBar use java thread
    Thread updateSeek; // is a thread

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        // Get xml object into java by using object id
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        // Give Intent from main activity
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        // getting song list into arraylist from songList
        songs = (ArrayList) bundle.getParcelableArrayList("songList");

        // Song Name
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent); // See Song Name in a textBox
        textView.setSelected(true);

        // Get Position from intent
        position = intent.getIntExtra("position", 0);

        // Bring Song from position
        Uri uri = Uri.parse(songs.get(position).toString());

        // TO Start media
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration()); // set max duration of seekbar

        // Adding SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        // Thread Program
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try{
                    while(currentPosition<mediaPlayer.getDuration()){ // if seekbar get lagged from current position
                        currentPosition = mediaPlayer.getCurrentPosition(); // update current position
                        seekBar.setProgress(currentPosition);
                        sleep(800); // Update in every 800 millisecond to not use extra resources of device
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start(); // start the update seekbar

        // Setting Event Listener for Play, Pause and Next Button

        // 1.) For Play Button
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play); // If song is playing then play button is showing
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });

        // 2.) For Previous Button
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop(); //stop the media player
                mediaPlayer.release(); // then release the media player
                if(position!=0){
                    position = position - 1; // get previous position song if position not 0
                }
                else{
                    position = songs.size() - 1; // for circular rotation of playlist
                }
                // again play the song using media player
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();

                // to update button image and textview
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
            }
        });

        // 3.) For Next Button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=songs.size()-1){
                    position = position + 1; // get next position song if position not size of songlist
                }
                else{
                    position = 0; // for circular rotation of playlist
                }

                // Again start the media player
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();

                // to update button image and textview
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);

            }
        });








    }
}