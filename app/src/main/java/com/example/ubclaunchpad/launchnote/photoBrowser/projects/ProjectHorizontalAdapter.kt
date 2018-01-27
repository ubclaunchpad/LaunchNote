package com.example.ubclaunchpad.launchnote.photoBrowser.projects

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.models.PicNote
import com.example.ubclaunchpad.launchnote.photoBrowser.ExpandPhotoActivity


/**
 * Created by sherryuan on 2018-01-09.
 */

class ProjectHorizontalAdapter() : RecyclerView.Adapter<ProjectHorizontalAdapter.ViewHolder>() {
    private lateinit var picNotes: List<PicNote>
    private lateinit var context: Context

    /**
     * The secondary constructor
     * It'll call the constructor inherited from RecyclerView.Adapter
     * and also set the picNotes and context fields
     */
    constructor(context: Context, picNotes: List<PicNote>) : this() {
        this.picNotes = picNotes
        this.context = context
    }

    fun setPicNotes(newPicNotes: List<PicNote>) {
        if (picNotes !== newPicNotes) {
            picNotes = newPicNotes
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picNote = picNotes[position]
        holder.description.text = picNote.description

        // get Bitmap from the picNote's URI and show it in an ImageView
        Glide.with(context)
                .asBitmap()
                .load(picNote.imageUri)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                        // set the image
                        holder.image.setImageBitmap(resource)
                        // TODO: replace with real description
                        // set the text
                        holder.description.text = "fake description"

                        holder.image.setOnClickListener {
                            val intent = Intent(context, ExpandPhotoActivity::class.java)
                            intent.putExtra(ExpandPhotoActivity.EXTRA_INTENT_IMAGE_URI, picNote.imageUri)  // pass in 1 imageuri to ExpandPhotoActivity
                            context.startActivity(intent)
                        }
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
        // our ViewHolder has two Views: an Imageview displaying the actual photo,
        // and a TextView displaying the description
        var description: TextView = view.findViewById<View>(R.id.description) as TextView
        var image: ImageView = view.findViewById<View>(R.id.image) as ImageView

    }
}