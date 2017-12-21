package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.chongchen.top10downloder.R;

import java.util.List;

import models.FeedEntry;

/**
 * Created by chongchen on 2017-12-21.
 */

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";

    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> dataList;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> dataList) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return this.dataList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = this.layoutInflater.inflate(this.layoutResource, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        FeedEntry data = this.dataList.get(position);

        vh.tvName.setText(data.getName());
        vh.tvArtist.setText(data.getArtist());
        vh.tvSummary.setText(data.getSummary());

        return convertView;
    }

    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        public ViewHolder(View v) {
            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist = v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);
        }
    }
}
