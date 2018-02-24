package com.example.ubclaunchpad.launchnote.addPhoto

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import com.example.ubclaunchpad.launchnote.models.PicNote
import com.example.ubclaunchpad.launchnote.photoBrowser.AllPhotosFragment
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class TakePhotoActivity : AppCompatActivity() {

    private lateinit var currentImagePath: String
    private lateinit var currentImageFile: File
    private lateinit var currentImageUri: Uri

    fun takePhoto(view: View) {
        takePhoto()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState?.containsKey(PHOTOFRAGMENTINIT) != true) {
            takePhoto()
        }
        savedInstanceState?.putBoolean(PHOTOFRAGMENTINIT, true)
    }

    private fun takePhoto() {
        // Intent to open up Android's camera
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // If it can be handled ...
        if (takePhotoIntent.resolveActivity(packageManager) != null) {
            // File where the image goes
            var imageFile: File? = null
            try {
                imageFile = createImageFile()
            } catch (e: IOException) {
                Toast.makeText(this, "Cannot save file", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }

            // If the image file was created with no problems ...
            if (imageFile != null) {
                // Get URI from file and pass it as an extra to the intent, then start intent
                val imageURI = FileProvider.getUriForFile(this, AUTHORITY, imageFile)
                currentImageUri = imageURI;
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST_CODE)
            }
        }
    }

    private fun compressImage(): Uri {
        // Compress the images in such a way that we are able to expand them correctly later on
        val maxSize  = maxOf(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels) / AllPhotosFragment.NUM_COLUMNS_PORTRAIT
        val compressedBmp = Glide.with(applicationContext)
                .asBitmap()
                .load(currentImageUri)
                .apply(RequestOptions.bitmapTransform(CenterInside()))
                .apply(RequestOptions().override(maxSize)).submit().get()
        // Creates the temp file that we write to
        val f = createImageFile(true)
        var out: FileOutputStream? = null;
        try {
            out = FileOutputStream(f)
            compressedBmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Couldn't compress file to small image, using large one!", Toast.LENGTH_SHORT).show()
            }
        }
        Log.d("TakePhotoActivity", "Finished compressing file")
        return if(out == null) currentImageUri else FileProvider.getUriForFile(this, AUTHORITY, f)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                /* If the picture was taken and saved to internal storage successfully,
                let's compress it and then save both URIs to the database
                 */
                Observable.just(requestCode)
                        .observeOn(Schedulers.io())
                        .flatMap({
                            Observable.create<Unit> {
                                val compressedImageUri = compressImage()
                                saveImgToDB(currentImageUri, compressedImageUri, it)
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            Log.d("TakePhotoActivity", "Clearing Glide cache")
                            Glide.get(this).clearMemory()
                        }
                openFragmentInfo()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                /* If the picture was not taken or not saved to internal storage, delete
                 the file that had been created (where the picture was supposed to go)
                 */
                Log.d("TakePhotoActivity", "Result canceled deleting temp image")
                currentImageFile.delete()
                /* Should something currentImagePath and currentImageUri be nulled
                 just in case???
                 */
            }
            finish()
        }
    }

    private fun saveImgToDB(imageURI: Uri, compressedUri: Uri, onDone: ObservableEmitter<Unit>) {
        // TODO: parse out the description
        // passing in empty string for now
        picNoteToSave = PicNote(imageURI.toString(), compressedUri.toString(),"", null)

        // insert image into database on a different thread
        LaunchNoteDatabase.getDatabase(this)?.let {
            Observable.fromCallable { it.picNoteDao().insert(picNoteToSave) }
                        .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        onDone.onNext(Unit)
                        onDone.onComplete()
                    }
        }
    }

    /**
     * This function gets called when we want to create a image file
     * @param forCompressed normally if we create a file we want the currentImageReference to point to it
     *                      but in the case of creating a compressed image we dont want that, furthermore
     *                      we append cmp to the image URI
     */
    @Throws(IOException::class)
    private fun createImageFile(forCompressed: Boolean = false): File {
        // First, create file name
        val timestamp = SimpleDateFormat(DATE_FORMAT).format(Date())
        val compressExt = if(forCompressed) "_cmp" else ""
        val fileName = JPEG + timestamp + compressExt
        // Directory where the file will be stored (check file_paths.xml)
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // Create file
        val image = File.createTempFile(fileName, IMAGE_EXTENSION, dir)
        if(!forCompressed) {
            currentImagePath = image.absolutePath
            currentImageFile = image
        }
        return image
    }

    private fun openFragmentInfo() {
        val bundle = Bundle()
        bundle.putString(URI_KEY, currentImageUri.toString())
        // Begin transaction
        val transaction = supportFragmentManager.beginTransaction()
        // Replace the contents of the container with the info fragment
        val fragment = PhotoInfoFragment()
        fragment.arguments = bundle
        transaction.add(R.id.take_photo_container, PhotoInfoFragment())
        // or transaction.replace(R.id.take_photo_container, PhotoInfoFragment())
        // Complete the changes added above
        transaction.commitAllowingStateLoss()

    }


    companion object {
        internal const val PHOTOFRAGMENTINIT = "PHOTOFRAGMENTINIT"
        internal const val TAKE_PHOTO_REQUEST_CODE = 1
        internal const val DATE_FORMAT = "yyyyMMdd_HHmmss"
        internal const val AUTHORITY = "com.example.ubclaunchpad.launchnote.FileProvider"
        internal const val JPEG = "JPEG_"
        internal const val IMAGE_EXTENSION = ".jpg"
        const val URI_KEY = "uri"
    }

}
