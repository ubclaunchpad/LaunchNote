package com.example.ubclaunchpad.launchnote.photoBrowser

import android.R.attr.uiOptions
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ubclaunchpad.launchnote.R
import com.github.chrisbanes.photoview.PhotoView
import android.widget.LinearLayout
import android.view.ViewGroup
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.support.v4.view.PagerAdapter
import android.widget.ImageView
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import com.example.ubclaunchpad.launchnote.models.PicNote
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ExpandPhotoActivity : AppCompatActivity() {
    private var picNoteIds: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expand_photo)

        val photoView = findViewById<PhotoView>(R.id.photo_view)
        val imageUri = intent.getStringExtra(EXTRA_INTENT_IMAGE_URI)  // receives image uri
        setImageIn(imageUri, photoView)
        fullScreen()

    }

    private fun setImageIn(uri: String, dest: PhotoView) {
        Glide.with(this)
                .asBitmap()
                .load(uri)
                .into<SimpleTarget<Bitmap>>(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                        // have photoView display the loaded Bitmap
                        dest.setImageBitmap(resource)
                    }
                })
    }

    fun fullScreen() {
        val uiOptions = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions
        val isImmersiveModeEnabled = isImmersiveModeEnabled()   // built-in

        if (isImmersiveModeEnabled) {
            Log.i("TEST", "Turning immersive mode mode off. ")
        } else {
            Log.i("TEST", "Turning immersive mode mode on.")
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        window.decorView.systemUiVisibility = newUiOptions

    }

    private fun loadImage(id: Int): PicNote? {
        val db = LaunchNoteDatabase.getDatabase(this);
        if (db == null) return null;
        return db.picNoteDao().findById(id.toString()).firstElement().blockingGet().first()
    }

    private fun loadImages() {
        LaunchNoteDatabase.getDatabase(this)?.let {
            it.picNoteDao().loadAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { dbPicNotes ->
                        // get current list of pic note IDs
                        picNoteIds.clear()
                        for (next in dbPicNotes) {
                            picNoteIds.add(next.id)
                        }
                    }
        }
    }

    internal inner class CustomPagerAdapter(var mContext: Context) : PagerAdapter() {



        var mLayoutInflater: LayoutInflater

        init {
            mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        override fun getCount(): Int {
            return picNoteIds.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as LinearLayout
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = mLayoutInflater.inflate(R.layout.element_picnote, container, false)

            val photoView = itemView.findViewById<PhotoView>(R.id.photo_view)
            val id = picNoteIds[position]

            val picnote = loadImage(id);
            if (picnote != null) {
                if (picnote.image != null) {
                    photoView.setImageBitmap(picnote.image)
                } else {
                    setImageIn(picnote.imageUri, photoView)
                }
            }

            container.addView(itemView)

            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as LinearLayout)
        }
    }

    private fun isImmersiveModeEnabled(): Boolean {
        return uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY === uiOptions

    }

    companion object {
        const val EXTRA_INTENT_IMAGE_URI = "INTENT_IMAGE_URI"
    }
}
