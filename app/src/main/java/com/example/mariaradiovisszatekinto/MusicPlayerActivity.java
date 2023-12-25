package com.example.mariaradiovisszatekinto;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTextView, currentTimeTextView, totalTimeTextView;
    SeekBar seekBar;
    ImageView pausePlay, nextButton, previousButton, musicIcon, backButton;
    ArrayList<AudioModel> recordingList;
    public static AudioModel currentRecording = new AudioModel("", "", "");
    public MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mediaPlayer = MyMediaPlayer.getInstance();

        titleTextView = findViewById(R.id.song_title);
        currentTimeTextView = findViewById(R.id.current_time);
        totalTimeTextView = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextButton = findViewById(R.id.next);
        previousButton = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);
        backButton = findViewById(R.id.downloadPageBackButton);

        titleTextView.setSelected(true);

        recordingList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithRecording();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTextView.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if (mediaPlayer.isPlaying()) {
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_24);
                    } else {
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null && b) {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setResourcesWithRecording() {

        if (! currentRecording.equals( recordingList.get(MyMediaPlayer.currentIndex) )) {
            currentRecording = recordingList.get(MyMediaPlayer.currentIndex);
            playRecording();
        } else {
            seekBar.setMax(mediaPlayer.getDuration());
        }

        titleTextView.setText(currentRecording.getTitle());

        totalTimeTextView.setText(convertToMMSS(currentRecording.getDuration()));

        pausePlay.setOnClickListener(v -> pausePlay());
        nextButton.setOnClickListener(v -> playNextRecording());
        previousButton.setOnClickListener(v -> playPreviousRecording());

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void playRecording() {
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(currentRecording.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playNextRecording() {

        if (MyMediaPlayer.currentIndex == recordingList.size() - 1) {
            return;
        }

        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourcesWithRecording();
    }

    private void playPreviousRecording() {
        if (MyMediaPlayer.currentIndex == 0) {
            return;
        }

        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithRecording();
    }

    private void pausePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    public static String convertToMMSS(String duration) {
        long millis = Long.parseLong(duration);
        return String.format(Locale.ENGLISH,"%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
                );
    }

}