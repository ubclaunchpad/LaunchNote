package com.example.ubclaunchpad.launchnote.recyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.ubclaunchpad.launchnote.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<AndroidVersion> androidVersion;
    private Context context;


    public DataAdapter(Context context, ArrayList<AndroidVersion> android) {
        this.androidVersion = android;
        this.context = context;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {

        viewHolder.tv_android.setText(androidVersion.get(i).getAndroid_version_name());

        Glide.with(context)
                // .asBitmap()
                .load(androidVersion.get(i).getAndroid_image_url())
                .apply(new RequestOptions().override(600,200))
                .into(viewHolder.img_android);

        // Picasso.with(context).load(androidVersion.get(i).getAndroid_image_url()).resize(240,220).into(viewHolder.img_android);

    }
    // changed dimensions to be more square, taking into account of size of text box





    @Override
    public int getItemCount() {
        return androidVersion.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_android;
        private ImageView img_android;
        public ViewHolder(View view) {
            super(view);

            tv_android = (TextView)view.findViewById(R.id.tv_android);
            img_android = (ImageView) view.findViewById(R.id.img_android);
        }
    }

}