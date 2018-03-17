package com.example.ubclaunchpad.launchnote.photoBrowser

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.models.PicNote


// For reference: https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder.html
class AllPhotosAdapter(): RecyclerView.Adapter<AllPhotosAdapter.ViewHolder>() {

    private lateinit var picNotes: List<PicNote>
    private lateinit var context: Context
    private lateinit var picNotesSelected: Set<PicNote>
    // Size of every scaled image that we are going to put on the view
    private var scale: Int = 500

    var onOnImageActionListener: onImageActionListener? = null;

    /**
     * The secondary constructor
     * It'll call the constructor inherited from RecyclerView.Adapter
     * and also set the picNotes and context fields
     */
    constructor(context: Context, picNotes: List<PicNote>, picNotesSelected: Set<PicNote>) : this() {
        this.scale = maxOf(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.heightPixels) / AllPhotosFragment.NUM_COLUMNS_PORTRAIT
        this.picNotes = picNotes
        this.context = context
        this.picNotesSelected = picNotesSelected
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picNote = picNotes[position]
        // get Bitmap from the picNote's URI and show it in an ImageView
        Glide.with(context)
                .asBitmap()
                .apply(RequestOptions().override(this.scale))
                .load( if (picNote.compressedImageUri == "") picNote.imageUri else picNote.compressedImageUri)
                .into(holder.image)

        // set the text
        holder.description.text = picNote.description

        holder.image.setOnClickListener {
            if(picNotesSelected.isNotEmpty()){
                if(picNotesSelected.contains(picNote)){
                    onOnImageActionListener?.onImageDeselected(picNote)
                } else {
                    onOnImageActionListener?.onImageSelected(picNote)
                }
            } else {
                val intent = Intent(context, ExpandPhotoActivity::class.java)
                intent.putExtra(ExpandPhotoActivity.EXTRA_INTENT_IMAGE_URI, picNote.imageUri)  // pass in 1 imageuri to ExpandPhotoActivity
                intent.putExtra(ExpandPhotoActivity.EXTRA_INTENT_IMAGE_ID, picNote.id)
                context.startActivity(intent)
            }
        }

        holder.image.setOnLongClickListener {
            onOnImageActionListener?.onImageSelected(picNote)
            true
        }

        if(picNotesSelected.contains(picNote)) {
            Log.i("INFO", "Marking " + picNote + " as selected!")
            val color = ContextCompat.getColor(context, R.color.imageSelectionMask)
            holder.image.setColorFilter( color, PorterDuff.Mode.DARKEN )
        } else {
            holder.image.clearColorFilter()
        }
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
        val description: TextView = view.findViewById<View>(R.id.photoBrowserDescription) as TextView
        val image: ImageView = view.findViewById<View>(R.id.photo_browser_image_view) as ImageView

    }

    interface onImageActionListener {
        fun onImageSelected(picNote: PicNote)
        fun onImageDeselected(picNote: PicNote)
    }
}