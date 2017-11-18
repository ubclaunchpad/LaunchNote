package com.example.ubclaunchpad.launchnote.recyclerView

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.models.PicNote

import java.util.ArrayList
import android.widget.Toast
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.example.ubclaunchpad.launchnote.photoBrowser.ExpandPhotoActivity


class DataAdapter(private val context: Context, private val pictures: ArrayList<PicNote>) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DataAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: DataAdapter.ViewHolder, i: Int) {
        // i is the index of the image from some magical wonderland
        viewHolder.txt.text = pictures[i].description

        Glide.with(context)
                // .asBitmap()
                .load(pictures[i].imageUri)
                .apply(RequestOptions().override(600, 400))
                .into(viewHolder.image)

        viewHolder.image.setOnClickListener({
            val intent = Intent(context, ExpandPhotoActivity::class.java)
            intent.putExtra("INTENT_IMAGE_URI", pictures[i].imageUri)  // pass in 1 imageuri to ExpandPhotoActivity
            context.startActivity(intent)
        })

        // Picasso.with(context).load(pictures.get(i).getImageUrl()).resize(240,220).into(viewHolder.image);
    }
    // changed dimensions to be more square, taking into account of size of text box


    override fun getItemCount(): Int {
        return pictures.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txt = view.findViewById<TextView>(R.id.tv_android)!!     // viewholder is data corresponding to box
        val image = view.findViewById<ImageView>(R.id.img_android)!!
        val root = view.findViewById<LinearLayout>(R.id.root)!!     // root is the actual box
    }
}