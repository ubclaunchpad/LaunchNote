package com.example.ubclaunchpad.launchnote.gallery

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.database.PicNoteDatabase
import com.example.ubclaunchpad.launchnote.models.PicNote

import butterknife.ButterKnife
import butterknife.OnClick
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Activity for selecting an image from photo gallery
 */
class GalleryActivity : Activity() {
    lateinit var photoView: ImageView
    var photoBitmap: Bitmap? = null
    var photoUri: Uri? = null
    var picNoteToSave: PicNote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        photoView = findViewById(R.id.imgView)
        ButterKnife.bind(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // when an Image is picked
        if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK && null != data) {
            // get Image from data
            photoUri = data.data
            // load Bitmap using Glide
            Glide.with(this)
                    .asBitmap()
                    .load(photoUri)
                    .into<SimpleTarget<Bitmap>>(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                            // have photoView display the loaded Bitmap
                            photoView.setImageBitmap(resource)
                            // set photoBitmap to the loaded Bitmap
                            photoBitmap = resource
                        }
                    })

        } else {
            Toast.makeText(this, "You haven't picked an image", Toast.LENGTH_LONG).show()

        }
    }

    @OnClick(R.id.buttonLoadPicture)
    fun loadImageFromGallery(view: View) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, RESULT_LOAD_IMG)
    }

    @OnClick(R.id.buttonSavePicture)
    fun saveImageToDb(view: View) {
        if (photoUri != null && photoBitmap != null) {
            // TODO: parse out the description
            // passing in empty string for now
            picNoteToSave = PicNote(photoUri.toString(), "", photoBitmap)

            // insert image into database on a different thread
            PicNoteDatabase.getDatabase(this)?.let {
                Observable.fromCallable { it.picNoteDao().insert(picNoteToSave) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
            }

            finish()
        } else {
            Toast.makeText(this, "You haven't picked an image", Toast.LENGTH_LONG).show()
        }
    }

    companion object {

        private val RESULT_LOAD_IMG = 1
    }
}
