package parsers;

import java.util.ArrayList;

import models.FeedEntry;

/**
 * Created by chongchen on 2017-12-20.
 */

public class FeedEntryParser {
    private static final String TAG = "FeedEntryParser";

    private ArrayList<FeedEntry> feedEntries;

    public FeedEntryParser() {
        this.feedEntries = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getFeedEntries() {
        return feedEntries;
    }

}
