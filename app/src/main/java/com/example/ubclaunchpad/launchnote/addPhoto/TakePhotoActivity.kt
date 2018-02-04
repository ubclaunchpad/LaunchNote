package com.example.ubclaunchpad.launchnote.addPhoto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.ubclaunchpad.launchnote.BaseActivity
import com.example.ubclaunchpad.launchnote.R

import com.example.ubclaunchpad.launchnote.database.PicNoteDatabase
import com.example.ubclaunchpad.launchnote.models.PicNote
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class TakePhotoActivity : BaseActivity(), PhotoInfoFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_take_photo
    }

    private lateinit var currentImagePath: String
    private lateinit var currentImageFile: File
    private lateinit var currentImageUri: Uri
    var picNoteToSave: PicNote? = null

    fun takePhoto(view: View) {
        /*
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
                val imageURI = FileProvider.getUriForFile(this, AUTHORITY, imageFile)
                currentImageUri = imageURI;
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST_CODE)
            }
        }
        */
        openFragmentInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
            //finish() // ??
        }
    }

    private fun saveImgToDB(imageURI: Uri) {
        // TODO: parse out the description
        // passing in empty string for now
        Handler().post(Runnable() {
            fun run() {
                openFragmentInfo()
            }
        })
        picNoteToSave = PicNote(imageURI.toString(), "", "")

        // insert image into database on a different thread
        PicNoteDatabase.getDatabase(this)?.let {
            Observable.fromCallable { it.picNoteDao().insert(picNoteToSave) }
                        .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
        //finish()

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // First, create file name
        val timestamp = SimpleDateFormat(DATE_FORMAT).format(Date())
        val fileName = JPEG + timestamp
        // Directory where the file will be stored (check file_paths.xml)
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // Create file
        val image = File.createTempFile(fileName, IMAGE_EXTENSION, dir)
        currentImagePath = image.absolutePath
        currentImageFile = image
        return image
    }

    private fun openFragmentInfo() {
        // Begin the transaction
        val transaction = supportFragmentManager.beginTransaction();
        // Replace the contents of the container with the new fragment
        transaction.add(R.id.take_photo_container, PhotoInfoFragment());
        // or transaction.replace(R.id.take_photo_container, PhotoInfoFragment());
        // Complete the changes added above
        transaction.commit();

    }


    companion object {

        internal const val TAKE_PHOTO_REQUEST_CODE = 1
        internal const val DATE_FORMAT = "yyyyMMdd_HHmmss"
        internal const val AUTHORITY = "com.example.ubclaunchpad.launchnote.FileProvider"
        internal const val JPEG = "JPEG_"
        internal const val IMAGE_EXTENSION = ".jpg"
    }

}
