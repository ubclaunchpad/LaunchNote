package com.example.ubclaunchpad.launchnote.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import com.example.ubclaunchpad.launchnote.addPhoto.TakePhotoActivity
import com.example.ubclaunchpad.launchnote.photoBrowser.AllPhotosFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


object PhotoUtils {
    /**
     * This function gets called when we want to create a image file
     * @param forCompressed normally if we create a file we want the currentImageReference to point to it
     *                      but in the case of creating a compressed image we dont want that, furthermore
     *                      we append cmp to the image URI
     */
    @Throws(IOException::class)
    fun createImageFile(context: Context, forCompressed: Boolean = false): File {
        // First, create file name
        val timestamp = SimpleDateFormat(TakePhotoActivity.DATE_FORMAT).format(Date())
        val compressExt = if (forCompressed) "_cmp" else ""
        val fileName = TakePhotoActivity.JPEG + timestamp + compressExt
        // Directory where the file will be stored (check file_paths.xml)
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // Create file
        return File.createTempFile(fileName, TakePhotoActivity.IMAGE_EXTENSION, dir)
    }

    fun compressImage(context: Context, photoUri: Uri): Uri {
        // Compress the images in such a way that we are able to expand them correctly later on
        val maxSize = maxOf(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.heightPixels) / AllPhotosFragment.NUM_COLUMNS_PORTRAIT
        val compressedBmp = Glide.with(context)
                .asBitmap()
                .load(photoUri)
                .apply(RequestOptions.bitmapTransform(CenterInside()))
                .apply(RequestOptions().override(maxSize)).submit().get()
        // Creates the temp file that we write to
        val f = createImageFile(context, true)
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(f)
            compressedBmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Couldn't compress file to small image, using large one!", Toast.LENGTH_SHORT).show()
            }
        }
        Log.d("TakePhotoActivity", "Finished compressing file")
        return if (out == null) photoUri else FileProvider.getUriForFile(context, TakePhotoActivity.AUTHORITY, f)
    }
}