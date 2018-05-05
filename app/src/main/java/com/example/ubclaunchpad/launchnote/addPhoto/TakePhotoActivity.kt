package com.example.ubclaunchpad.launchnote.addPhoto

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import com.example.ubclaunchpad.launchnote.BaseActivity
import com.example.ubclaunchpad.launchnote.models.PicNote
import com.example.ubclaunchpad.launchnote.photoBrowser.AllPhotosFragment
import com.example.ubclaunchpad.launchnote.utils.PhotoUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TakePhotoActivity : AppCompatActivity() {

    private var currentImageUri: Uri? = null

    fun takePhoto(view: View) {
        takePhoto()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!intent.hasExtra(PHOTOFRAGMENTINIT)) {
            takePhoto()
        }
        intent.putExtra(PHOTOFRAGMENTINIT, true)
        savedInstanceState?.let {
            it.getString(URI_KEY)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putString(URI_KEY, currentImageUri.toString())
    }

    private fun takePhoto() {
        // Intent to open up Android's camera
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // If it can be handled ...
        if (takePhotoIntent.resolveActivity(packageManager) != null) {
            // File where the image goes
            var imageFile: File? = null
            try {
                imageFile = PhotoUtils.createImageFile(this)
                // save the uncompressed image
            } catch (e: IOException) {
                Toast.makeText(this, "Cannot save file", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }

            // If the image file was created with no problems ...
            if (imageFile != null) {
                // Get URI from file and pass it as an extra to the intent, then start intent
                val imageURI = FileProvider.getUriForFile(this, AUTHORITY, imageFile)
                currentImageUri = imageURI
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                /* If the picture was taken and saved to internal storage successfully,
                let's compress it and then save both URIs to the database
                 */
                val pn = PicNote(currentImageUri.toString())
                Observable.just(requestCode)
                        .observeOn(Schedulers.io())
                        .flatMap({
                            Observable.create<Unit> {
                                val compressedImageUri = PhotoUtils.compressImage(this, currentImageUri!!)
                                pn.compressedImageUri = compressedImageUri.toString()
                                it.onNext(Unit)
                                it.onComplete()
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            Log.d("TakePhotoActivity", "Clearing Glide cache")
                            Glide.get(this).clearMemory()
                            val intent = Intent()
                            intent.putExtra(BaseActivity.PIC_NOTE_KEY, pn)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                /* If the picture was not taken or not saved to internal storage, delete
                 the file that had been created (where the picture was supposed to go)
                 */
                Log.d("TakePhotoActivity", "Result canceled deleting temp image")
                currentImageUri?.let {
                    File(it.toString()).delete()
                }
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    companion object {
        internal const val PHOTOFRAGMENTINIT = "PHOTOFRAGMENTINIT"
        const val TAKE_PHOTO_REQUEST_CODE = 45912
        internal const val DATE_FORMAT = "yyyyMMdd_HHmmss"
        internal const val AUTHORITY = "com.example.ubclaunchpad.launchnote.FileProvider"
        internal const val JPEG = "JPEG_"
        internal const val IMAGE_EXTENSION = ".jpg"
        const val URI_KEY = "uri"
    }

}
