package com.ioanghisoi.biskit;

import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by ioanghisoi on 1/29/18.
 */

public class PlayURL extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            try {
                URL url = new URL(
                        "http://81.150.1.153:8000/stream.mp3");
                ParsingUrlData streaming = new ParsingUrlData();
                ParsingUrlData.TrackData trackData = streaming.getTrackDetails(url);
                return new String[] {trackData.title, trackData.artist.substring(7).toUpperCase() };
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new String[] {"Biskit Radio", ""};
        }

    }
