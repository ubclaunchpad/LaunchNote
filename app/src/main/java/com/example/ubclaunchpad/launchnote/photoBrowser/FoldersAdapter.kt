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

        if (folder.picNoteIds.size > 0) {
            LaunchNoteDatabase.getDatabase(context)?.let {
                it.picNoteDao().findById(0)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { picNote1 ->

                            if (picNote1.size > 0) {
                                Glide.with(context)
                                        .asBitmap()
                                        .load(if (picNote1[0].compressedImageUri == "") picNote1[0].imageUri else picNote1[0].compressedImageUri)
                                        .into(holder.img1)
                            }
                        }
            }
        }
        if (folder.picNoteIds.size > 1) {
            LaunchNoteDatabase.getDatabase(context)?.let {
                it.picNoteDao().findById(folder.picNoteIds[1])
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { picNote2 ->
                            Glide.with(context)
                                    .asBitmap()
                                    .load(if (picNote2[0].compressedImageUri == "") picNote2[0].imageUri else picNote2[0].compressedImageUri)
                                    .into(holder.img2)
                        }
            }
        }
        if (folder.picNoteIds.size > 2) {
            LaunchNoteDatabase.getDatabase(context)?.let {
                it.picNoteDao().findById(folder.picNoteIds[2])
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { picNote3 ->
                            Glide.with(context)
                                    .asBitmap()
                                    .load(if (picNote3[0].compressedImageUri == "") picNote3[0].imageUri else picNote3[0].compressedImageUri)
                                    .into(holder.img3)
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
