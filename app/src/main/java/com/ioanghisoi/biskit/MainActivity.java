package com.ioanghisoi.biskit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import es.claucookie.miniequalizerlibrary.EqualizerView;

public class MainActivity extends Activity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private MediaPlayer player;
    EqualizerView equalizer;
    ImageButton playButton, share, love;
    ProgressDialog nDialog;
    TextView title, artist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isNetworkAvailable()) {
            Toast.makeText(this, (String)"OMG! No internet :( ",
                    Toast.LENGTH_LONG).show();
        }

        nDialog = new ProgressDialog(this);
        nDialog.setMessage("Getting za' music...");
        nDialog.setTitle("Making the biskits");
        nDialog.setProgressStyle(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);
        nDialog.show();

        equalizer = (EqualizerView) findViewById(R.id.equalizer_view);
        playButton = (ImageButton) findViewById(R.id.play);
        share = (ImageButton) findViewById(R.id.share);
        love = (ImageButton) findViewById(R.id.love);
        title = (TextView) findViewById(R.id.title);
        artist = (TextView) findViewById(R.id.artist);

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource("http://81.150.1.153:8000/stream.mp3");
            player.setOnErrorListener(this);
            player.setOnPreparedListener(this);
            player.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(player.isPlaying()) {
                        player.pause();
                        equalizer.stopBars();
                        playButton.setImageResource(R.drawable.play_button);
                    } else {
                        player.start();
                        equalizer.animateBars();
                        playButton.setImageResource(R.drawable.pause_button);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://biskitradio.co.uk/";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://biskitradio.co.uk/";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        new ProgressBar(this);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
        player = null;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepared(MediaPlayer play) {
        play.start();
        Log.d("DUREAZAAAAA", String.valueOf(player.getTrackInfo()));
        nDialog.hide();
        playButton.setImageResource(R.drawable.pause_button);
        equalizer.animateBars();
        String xxx = String.valueOf(play.getDuration());
        Log.d("DUREAZAAAAA", String.valueOf(player.getDuration()));
        new PlayURL().execute();

        try {
            String[] receivedData = new PlayURL().execute().get();
            title.setText(receivedData[0]);
            artist.setText(receivedData[1]);
            Log.e("Song Artist Name ", receivedData[0]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        return false;
    }
}
