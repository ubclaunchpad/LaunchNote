package com.example.ubclaunchpad.launchnote

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.example.ubclaunchpad.launchnote.database.PicNoteDatabase
import com.example.ubclaunchpad.launchnote.models.PicNote
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class TakePhotoActivity : AppCompatActivity() {
    internal lateinit var currentImagePath: String
    internal lateinit var currentImageFile: File
    internal lateinit var currentImageUri: Uri
    var picNoteToSave: PicNote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)
    }

    fun takePhoto(view: View) {
        // Intent to open up Android's camera
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // If it can be handled ...
        if (takePhotoIntent.resolveActivity(packageManager) != null) {
            // File where the image goes
            var imageFile: File? = null
            try {
                imageFile = createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // If the image file was created with no problems ...
            if (imageFile != null) {
                // Get URI from file and pass it as an extra to the intent, then start intent
                val imageURI = FileProvider.getUriForFile(this, "com.example.ubclaunchpad.launchnote.FileProvider", imageFile)
                currentImageUri = imageURI;
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                /* If the picture was taken and saved to internal storage successfully,
                let's save its URI to the database
                 */
                saveImgToDB(currentImageUri)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                /* I the picture was not taken or not saved to internal storage, delete
                 the file that had been created (where the picture was supposed to go)
                 */
                currentImageFile.delete()
                /* Should something currentImagePath and currentImageUri be nulled
                 just in case???
                 */
            }
        }
    }

    private fun saveImgToDB(imageURI: Uri) {
        // TODO: parse out the description
        // passing in empty string for now
        picNoteToSave = PicNote(imageURI.toString(), "", null)

        // insert image into database on a different thread
        PicNoteDatabase.getDatabase(this)?.let {
            Observable.fromCallable { it.picNoteDao().insert(picNoteToSave) }
                        .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
        finish()

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // First, create file name
        val timestamp = SimpleDateFormat(DATE_FORMAT).format(Date())
        val fileName = "JPEG_" + timestamp
        // Directory where the file will be stored (check file_paths.xml)
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // Create file
        val image = File.createTempFile(fileName, ".jpg", dir)
        currentImagePath = image.absolutePath
        currentImageFile = image
        return image
    }

    companion object {

        internal val TAKE_PHOTO_REQUEST_CODE = 1
        internal val DATE_FORMAT = "yyyyMMdd_HHmmss"
    }

}
