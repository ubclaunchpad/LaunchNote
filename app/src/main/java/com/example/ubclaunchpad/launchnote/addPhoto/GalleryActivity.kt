package com.example.ubclaunchpad.launchnote.addPhoto

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ubclaunchpad.launchnote.BaseActivity
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import com.example.ubclaunchpad.launchnote.models.PicNote
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Activity for selecting an image from photo gallery
 */
class GalleryActivity : BaseActivity() {

    lateinit var photoView: ImageView
    var photoBitmap: Bitmap? = null
    var photoUri: Uri? = null
    var picNoteToSave: PicNote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoView = findViewById(R.id.imgView)
        if(savedInstanceState?.containsKey(GALLERYBROWSEOPEN) != true) {
            loadImageFromGallery()
        }
        savedInstanceState?.putBoolean(GALLERYBROWSEOPEN, true)
        ButterKnife.bind(this)
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.getItem(ADD_MENU_ITEM).isChecked = true
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_gallery
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        // when an Image is picked
        if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK && data != null) {
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
            Toast.makeText(this, "You didn't select an image!", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    @OnClick(R.id.buttonLoadPicture)
    fun loadImageFromGallery(view: View) {
        loadImageFromGallery()
    }

    private fun loadImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG)
    }

    @OnClick(R.id.buttonSavePicture)
    fun saveImageToDb(view: View) {
        if (photoUri != null && photoBitmap != null) {
            // TODO: parse out the description
            // passing in empty string for now
            // todo vpineda optimize this save
            picNoteToSave = PicNote(photoUri.toString(), photoUri.toString(),"", "")

            // insert image into database on a different thread
            LaunchNoteDatabase.getDatabase(this)?.let {
                Observable.fromCallable { it.picNoteDao().insert(picNoteToSave) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
            }
            val intent = Intent()
            intent.putExtra(BaseActivity.PIC_NOTE_KEY, picNoteToSave)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            Toast.makeText(this, "You haven't picked an image", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val GALLERY_ACTIVITY_REQ_CODE = 434
        internal const val GALLERYBROWSEOPEN = "GALLERYBROWSEOPEN"
        private val RESULT_LOAD_IMG = 1
    }
}
