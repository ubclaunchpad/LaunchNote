package com.example.ubclaunchpad.launchnote.photoBrowser

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import com.bumptech.glide.Glide
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import com.example.ubclaunchpad.launchnote.edit.PhotoInfoActivity
import com.example.ubclaunchpad.launchnote.models.PicNote
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Fragment displaying all photos in a grid format
 */
class AllPhotosFragment : Fragment() {

    // a MutableList in Kotlin is the same as a List in Java
    // Kotlin also has a List class, but it's immutable and doesn't let you add/remove items
    private val picNotes: MutableList<PicNote> = mutableListOf()
    private val picNotesSelected: MutableSet<PicNote> = mutableSetOf()
    var onListener: OnEditPhotoMode? = null

    private lateinit var adapter: AllPhotosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_all_photos, null)
    }

    override fun onResume() {
        super.onResume()
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            initViews(NUM_COLUMNS_PORTRAIT) // 3 columns while straight
        } else {
            initViews(NUM_COLUMNS_LANDSCAPE) // 4 columns while rotated
        }
        loadImages()
    }

    /**
     * Removes the selected images from the database, clears the selection and forces a rerender
     */
    fun removeSelection() {
        Flowable.fromCallable {
            picNotesSelected.forEach { pn ->
                Log.i("DEL", "Deleting: " + pn)
                val success = PicNote.deleteFromDb(context, pn)
                if (!success) Toast.makeText(context, "Cannot delete ${pn.id}", Toast.LENGTH_LONG).show()
            }
            picNotesSelected.clear()
        }.subscribeOn(Schedulers.io()).subscribe {
            rerenderPhotoGrid()
            onListener?.onEditPhotoMode(false)
        }
    }

    fun openEditView() {
        if (picNotesSelected.size != 1) {
            Toast.makeText(context, "Cannot edit more than 1 image at a time, for now", Toast.LENGTH_SHORT).show()
        } else {
            val i = Intent(context, PhotoInfoActivity::class.java)
            i.putExtra(PhotoInfoActivity.PIC_NOTE_ARG, picNotesSelected.first())
            picNotesSelected.clear()
            onListener?.onEditPhotoMode(false)
            startActivity(i)
        }
    }

    fun saveSelection() {
        Flowable.fromCallable {
            LaunchNoteDatabase.getDatabase(activity)?.let {
                picNotesSelected.forEach { pn ->
                    Log.i("SAV", "saving: " + pn)
                    if (ContextCompat.checkSelfPermission(getActivity(),
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        var bitmap: Bitmap;
                        bitmap = Glide.with(activity)
                                .asBitmap()
                                .load(pn.imageUri)
                                .submit()
                                .get();

                        Log.i("SAV", "BITMAP" + bitmap)
                        MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "yourTitle", "yourDescription");

                    } else {
                        requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);

                    }
                }
            }

        }.subscribeOn(Schedulers.io()).subscribe {
            onListener?.onEditPhotoMode(false)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 1) {
                saveSelection();

            }
        }
    }

    fun cancelSelection() {
        picNotesSelected.clear()
        onListener?.onEditPhotoMode(false)
        rerenderPhotoGrid()
    }

    private fun rerenderPhotoGrid() {
        // TODO vpineda depending on the view the load images method might change
        loadImages()
        view?.post {
            adapter.notifyDataSetChanged()
        }
    }

    private fun loadImages() {
        LaunchNoteDatabase.getDatabase(activity)?.let {
            it.picNoteDao().loadAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { dbPicNotes ->
                        // clear the currently saved picNotes
                        // and replace them with new ones from the database
                        picNotes.clear()
                        for (next in dbPicNotes) {
                            picNotes.add(next)
                        }
                        adapter.notifyDataSetChanged()
                    }
        }
    }

    private fun initViews(numColumn: Int) {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.card_recycler_view)
        recyclerView.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(activity, numColumn)
        recyclerView.layoutManager = layoutManager

        adapter = AllPhotosAdapter(activity, picNotes, picNotesSelected)
        adapter.onOnImageActionListener = onLongPressImage
        recyclerView.adapter = adapter
    }

    /**
     * Callback object for long press image
     */
    private val onLongPressImage = object : AllPhotosAdapter.onImageActionListener {
        override fun onImageSelected(picNote: PicNote) {
            picNotesSelected.add(picNote)
            onListener!!.onEditPhotoMode(true, picNotesSelected)
            // TODO vpineda re-render here is a bit harsh, we might be able to optimize this to just rerender all if we change to selection mode
            rerenderPhotoGrid()
        }

        override fun onImageDeselected(picNote: PicNote) {
            picNotesSelected.remove(picNote)
            if (picNotesSelected.isEmpty()) {
                onListener!!.onEditPhotoMode(false, picNotesSelected)
            } else {
                onListener!!.onEditPhotoMode(true, picNotesSelected)
            }
            // TODO vpineda re-render here is a bit harsh, we might be able to optimize this to just rerender all if we change to selection mode
            rerenderPhotoGrid()
        }
    }

    companion object {
        // declaring a const val in a Kotlin class's companion object is equivalent to
        // declaring a static final variable in Java
        // Kotlin doesn't have the static keyword, so companion objects are where you put static things
        const val NUM_COLUMNS_PORTRAIT = 3
        const val NUM_COLUMNS_LANDSCAPE = 4
    }

    interface OnEditPhotoMode {
        fun onEditPhotoMode(isActiveEdit: Boolean, imagesSelected: Set<PicNote> = mutableSetOf())
    }
}