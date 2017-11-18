package com.example.ubclaunchpad.launchnote.recyclerView

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.models.PicNote


class AllPhotosAdapter(): RecyclerView.Adapter<AllPhotosAdapter.ViewHolder>() {

    private lateinit var picNotes: List<PicNote>
    private lateinit var context: Context


    constructor(context: Context, picNotes: List<PicNote>) : this() {
        this.picNotes = picNotes
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picNote = picNotes[position]
        holder.label.text = picNote.description

        Glide.with(context)
                .asBitmap()
                .load(picNote.imageUri)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                        // set photoBitmap to the loaded Bitmap
                        holder.image.setImageBitmap(resource)
                    }
                })
    }

    override fun getItemCount(): Int {
        return picNotes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var label: TextView = view.findViewById<View>(R.id.label) as TextView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView

    }
}