package com.example.ubclaunchpad.launchnote.photoBrowser

import android.R.attr.uiOptions
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


class ExpandPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expand_photo)

        val photoView = findViewById<PhotoView>(R.id.photo_view)
        val imageUri = intent.getStringExtra(EXTRA_INTENT_IMAGE_URI)  // receives image uri
        Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .into<SimpleTarget<Bitmap>>(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                        // have photoView display the loaded Bitmap
                        photoView.setImageBitmap(resource)
                    }
                })
        fullScreen()

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


    private fun isImmersiveModeEnabled(): Boolean {
        return uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY === uiOptions

    }

    companion object {
        const val EXTRA_INTENT_IMAGE_URI = "INTENT_IMAGE_URI"
    }
}
