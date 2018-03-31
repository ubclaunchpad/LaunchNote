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
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import com.example.ubclaunchpad.launchnote.models.Folder
import com.example.ubclaunchpad.launchnote.models.PicNote
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by sherryuan on 2018-03-24.
 */
class FoldersAdapter() : RecyclerView.Adapter<FoldersAdapter.ViewHolder>() {

    private lateinit var folders: List<Folder>
    private lateinit var context: Context

    /**
     * The secondary constructor
     * It'll call the constructor inherited from RecyclerView.Adapter
     * and also set the picNotes and context fields
     */
    constructor(context: Context, folders: List<Folder>) : this() {
        this.folders = folders
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folders[position]

        // load first 3 photos in the folder
        (0..2).filter { folder.picNoteIds.size > it }
                .forEach { i ->
                    LaunchNoteDatabase.getDatabase(context)?.let {
                        it.picNoteDao().findById(folder.picNoteIds[i])
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { picNote ->
                                    if (picNote.size > 0) {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(if (picNote[0].compressedImageUri == "") picNote[0].imageUri else picNote[0].compressedImageUri)
                                                .into(when (i) {
                                                    0 -> holder.img1
                                                    1 -> holder.img2
                                                    2 -> holder.img3
                                                    // shouldn't ever reach the else case since forEach is called on 0 to 2
                                                    else -> holder.img1
                                                })
                                    }
                                }
                    }
                }

        holder.folderName.text = folder.name
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder_row, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img1: ImageView = view.findViewById<View>(R.id.img1) as ImageView
        val img2: ImageView = view.findViewById<View>(R.id.img2) as ImageView
        val img3: ImageView = view.findViewById<View>(R.id.img3) as ImageView
        val folderName: TextView = view.findViewById<View>(R.id.folder_name) as TextView
    }
}
