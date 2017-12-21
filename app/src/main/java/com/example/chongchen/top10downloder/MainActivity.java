package com.example.chongchen.top10downloder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import adapters.FeedAdapter;
import models.FeedEntry;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView lvData;
    private String urlCached = "INVALIDATED";
    private String url;
    private int limit;


    private static final String STATE_URL = "url";
    private static final String STATE_LIMIT = "limit";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        lvData = findViewById(R.id.lvData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        switch(this.limit){
            case 10:
                menu.findItem(R.id.menu10).setChecked(true);
                break;
            case 25:
                menu.findItem(R.id.menu25).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuFree:
                url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.menuPaid:
                url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.menuSong:
                url = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.menu10:
            case R.id.menu25:
                if(item.isChecked()){
                    return true;
                }
                limit = 35 - limit;
                item.setChecked(true);
            case R.id.menuRefresh:
                this.urlCached = "INVALIDATED";
                break;
        }
        showDownloadData(String.format(this.url, limit));
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        this.url = sp.getString(STATE_URL, "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml");
        this.limit = sp.getInt(STATE_LIMIT, 10);
        super.onResume();
        showDownloadData(String.format(url, limit));
    }

    @Override
    protected void onPause() {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        sp.edit().putString(STATE_URL, this.url).putInt(STATE_LIMIT, this.limit).apply();

        super.onPause();
    }

    private void showDownloadData(String url){
        if(url == null && url.length() == 0){
            return;
        }
        if(this.url.equalsIgnoreCase(this.urlCached)){
            return;
        }
        DownloadData dd = new DownloadData();
        this.urlCached = this.url;
        dd.execute(url);
    }

    private class DownloadData extends AsyncTask<String, Void, ArrayList<FeedEntry>>{
        private static final String TAG = "DownloadData";
        @Override
        protected void onPostExecute(ArrayList<FeedEntry> list) {
            Log.d(TAG, "onPostExecute: length: " + list.size());
            super.onPostExecute(list);
            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.item_2, list);
            lvData.setAdapter(feedAdapter);
        }

        @Override
        protected ArrayList<FeedEntry> doInBackground(String... strings) {
            ArrayList<FeedEntry> response = null;
            try {
                response = downloadXml(strings[0]);
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
            }
            return response;
        }

        private ArrayList<FeedEntry> downloadXml(String urlPath) throws Exception{
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int code = connection.getResponseCode();
            Log.d(TAG, "downloadXml: status code: " + code);

            return parseFeedEntry(connection.getInputStream());
        }
    }

    private ArrayList<FeedEntry> parseFeedEntry(InputStream in) throws Exception{
        ArrayList<FeedEntry> dataList = new ArrayList<>();
        FeedEntry entry = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);
            for(int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next()){
                if(parser.getEventType() != XmlPullParser.START_TAG){
                    continue;
                }
                String name = parser.getName();

                if("entry".equalsIgnoreCase(name)){
                    entry = new FeedEntry();
                    dataList.add(entry);
                    continue;
                }

                if(entry != null){
                    switch (name){
                        case "name":
                            entry.setName(parser.nextText());
                            break;
                        case "artist":
                            entry.setArtist(parser.nextText());
                            break;
                        case "summary":
                            entry.setSummary(parser.nextText());
                            break;
                        case "image":
                            String height = parser.getAttributeValue(null, "height");
                            if("53".equalsIgnoreCase(height)){
                                entry.setImageURL(parser.nextText());
                            }
                            break;
                        case "releaseDate":
                            entry.setReleaseDate(parser.nextText());
                            break;
                    }
                }
            }
        }catch (Exception e){
            throw e;
        }finally {
            in.close();
        }

        return dataList;
    }
}


