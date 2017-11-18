package com.example.ubclaunchpad.launchnote.recyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ubclaunchpad.launchnote.R;
import com.example.ubclaunchpad.launchnote.models.PicNote;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<PicNote> pictures;
    private Context context;


    public DataAdapter(Context context, ArrayList<PicNote> android) {
        this.pictures = android;
        this.context = context;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {

        viewHolder.tv_picnote.setText(pictures.get(i).getDescription());

        Glide.with(context)
                // .asBitmap()
                .load(pictures.get(i).getImageUri())
                .apply(new RequestOptions().override(600,200))
                .into(viewHolder.img_picnote);

        // Picasso.with(context).load(pictures.get(i).getImageUrl()).resize(240,220).into(viewHolder.img_picnote);

    }
    // changed dimensions to be more square, taking into account of size of text box





    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_picnote;
        private ImageView img_picnote;
        public ViewHolder(View view) {
            super(view);

            tv_picnote = (TextView)view.findViewById(R.id.tv_android);
            img_picnote = (ImageView) view.findViewById(R.id.img_android);
        }
    }

}