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
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.claucookie.miniequalizerlibrary.EqualizerView;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private MediaPlayer player;
    EqualizerView equalizer;
    ImageButton playButton, share, love;
    ProgressDialog nDialog;
    TextView title, artist;
    SeekBar volume;
    AudioManager audioManager;

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
        volume = (SeekBar) findViewById(R.id.volume);

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
//                        equalizer.stopBars();
                        playButton.setImageResource(R.drawable.play_button);
                    } else {
                        player.start();
//                        equalizer.animateBars();
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

        try
        {
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volume.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volume.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isBusy = false;//this flag to indicate whether your async task completed or not
    private boolean stop = false;//this flag to indicate whether your button stop clicked
    private Handler handler = new Handler();

    public void startHandler()
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        if(!isBusy) {
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

                        };

                        if(!stop) startHandler();
                    }
                }, 5000);
            }
        };

        thread.start();

    }

    public void  animator() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                // this runs on a background thread
                Log.v(TAG, "Worker thread id:" + Thread.currentThread().getId());
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        equalizer.animateBars();
                    }
                });
            }
        });
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
//        equalizer.animateBars();
        equalizer.animateBars();
        String xxx = String.valueOf(play.getDuration());
        Log.d("DUREAZAAAAA", String.valueOf(player.getDuration()));
        startHandler();
//        new PlayURL().execute();
//
//        try {
//            String[] receivedData = new PlayURL().execute().get();
//            title.setText(receivedData[0]);
//            artist.setText(receivedData[1]);
//            Log.e("Song Artist Name ", receivedData[0]);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        return false;
    }
}
