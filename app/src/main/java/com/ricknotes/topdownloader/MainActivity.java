package com.ricknotes.topdownloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView mListView;
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private String feedCachedUrl = "INVALIDATED";
    public static final String STATE_FEEDURL = "feedUrl";
    public static final String STATE_FEEDLIMIT = "feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.xmlListView);

        if (savedInstanceState != null){
            feedUrl = savedInstanceState.getString(STATE_FEEDURL);
            feedLimit = savedInstanceState.getInt(STATE_FEEDLIMIT);
        }

        downloadUrl(String.format(feedUrl, feedLimit));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.feeds_menu, menu);
       if (feedLimit == 10){
           menu.findItem(R.id.mnu10).setChecked(true);
       }else {
           menu.findItem(R.id.mnu25).setChecked(true);
       }
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.mnuFree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid :
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if (!item.isChecked()){
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + "setting feed limit to " + feedLimit);
                }else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + "feed limit unchanged");
                }
                break;
            case R.id.mnuRefresh:
                feedCachedUrl = "INVALIDATED";
                break;
                default:
                    return super.onOptionsItemSelected(item);

        }
        downloadUrl(String.format(feedUrl, feedLimit));
        return true;
    }

    private void downloadUrl(String feedUrl) {
        if (!feedUrl.equals(feedCachedUrl)){
            Log.d(TAG, "downloadUrl: starting Asynctask");
            DownloadData downloadData = new DownloadData();
            downloadData.execute(feedUrl);
            feedCachedUrl = feedUrl;
            Log.d(TAG, "downloadUrl: Done");
        }else {
            Log.d(TAG, "downloadUrl: Url not changed");
        }

    }

    private  class DownloadData extends AsyncTask <String, Void , String> {
        private static final String TAG = "DownloadData";
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
         //   Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_records, parseApplications.getApplications());
            mListView.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null){
                Log.e(TAG, "doInBackground: Error downloading data");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();

            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was " + response);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                int charsRead;
                char[] inputBuffer = new char[500];
                while (true){
                    charsRead = reader.read(inputBuffer);
                    if (charsRead < 0 ){
                        break;
                    }
                    if (charsRead > 0 ){
                        xmlResult.append(String.copyValueOf(inputBuffer, 0 , charsRead));
                    }
                }
                reader.close();
                return xmlResult.toString();
            }catch (MalformedURLException e){
                Log.e(TAG, "downloadXML: Invalid URL " +e.getMessage());
            }catch (IOException e){
                Log.e(TAG, "downloadXML: IO Exception reading data " + e.getMessage());
            }catch (SecurityException e) {
                Log.e(TAG, "downloadXML: Security Exception, needs permission" + e.getMessage());
            }
            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_FEEDURL, feedUrl);
        outState.putInt(STATE_FEEDLIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }
}
