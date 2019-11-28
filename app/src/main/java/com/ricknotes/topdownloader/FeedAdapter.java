package com.ricknotes.topdownloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ricknotes.topdownloader.FeedEntry;
import com.ricknotes.topdownloader.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "com.ricknotes.topdownloader.FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater mLayoutInflater;
    private List<FeedEntry> applications;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = mLayoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FeedEntry currentApp = applications.get(position);
        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());
        String url = currentApp.getImageUrl();
        Picasso.get().load(url).centerInside().resize(200, 200).into(viewHolder.tvImage);
       // PicassoClient.downloadImage(currentApp.getImageUrl(), viewHolder.tvImage);

        return convertView;
    }
    private class ViewHolder{
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;
        final ImageView tvImage;

        ViewHolder (View v){
            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist= v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);
            this.tvImage = v.findViewById(R.id.tvImage);
        }
    }
}
