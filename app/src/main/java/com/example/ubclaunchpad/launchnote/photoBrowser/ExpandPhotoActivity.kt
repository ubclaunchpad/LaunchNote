package com.example.ubclaunchpad.launchnote.photoBrowser

import android.R.attr.uiOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import com.example.ubclaunchpad.launchnote.edit.PhotoInfoActivity
import com.example.ubclaunchpad.launchnote.models.PicNote
import com.example.ubclaunchpad.launchnote.toolbar.PhotoNavigatonToolbarFragment
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ExpandPhotoActivity : AppCompatActivity(), PhotoNavigatonToolbarFragment.OnButtonPressListener {

    private val picNotes: MutableList<PicNote> = mutableListOf()
    lateinit private var adapter: PhotoViewPagerAdapter
    lateinit private var viewPager: ViewPager
    lateinit private var toolbarFragment: PhotoNavigatonToolbarFragment

    private var defaultImageId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expand_photo)

        loadImages()

        viewPager = findViewById(R.id.expand_photo)

        adapter = PhotoViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        defaultImageId = intent.getIntExtra(EXTRA_INTENT_IMAGE_ID, 0)

        // set toolbar to EditMode
        toolbarFragment = supportFragmentManager.findFragmentById(R.id.expand_photo_toolbar_fragment) as PhotoNavigatonToolbarFragment
        toolbarFragment.setMode(PhotoNavigatonToolbarFragment.ToolbarMode.EditMode)

        fullScreen()
    }

    /**
     * When back button is pressed, clear Glide cache
     */
    override fun onBackPressed() {
        Observable.just {
            Glide.get(this).clearDiskCache()
            Glide.get(this).clearMemory()
        }
                .subscribeOn(Schedulers.io())
                .subscribe()

        super.onBackPressed()
    }

    override fun onButtonClicked(butonInfo: Int) {
        Log.i("INFO", "Clicked " + butonInfo)
        when (butonInfo) {
            R.id.edit_toolbar_back_btn -> {
                onBackPressed()
            }
            R.id.edit_toolbar_text_view -> {
                // do nothing
            }
            R.id.edit_toolbar_edit_desc_btn -> {
                onEditPressed()
            }
            R.id.edit_toolbar_delete_btn -> {
                onDeletePressed()
            }
            R.id.edit_toolbar_save_btn -> {
               saveSelection()
            }
        }
    }

    private fun saveSelection() {
        // get the id of the current item displayed in the ViewPager and delete it
        LaunchNoteDatabase
                .getDatabase(this)
                ?.picNoteDao()
                ?.findById(picNotes[viewPager.currentItem].id)
                ?.map { pn ->
                    pn.forEach {
                        Log.i("SAV", "saving: " + pn)
                        if (ContextCompat.checkSelfPermission(this,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                            var bitmap: Bitmap;
                            bitmap = Glide.with(this)
                                    .asBitmap()
                                    .load(pn[0].imageUri)
                                    .submit()
                                    .get();

                            Log.i("SAV", "BITMAP" + bitmap)
                            MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "yourTitle", "yourDescription");

                        } else {
                            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);

                        }
                    }

                }?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe()
        // exit full screen
        onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(requestCode ==1){
                saveSelection();

            }
        }
    }

    private fun fullScreen() {
        var newUiOptions = window.decorView.systemUiVisibility
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

    private fun loadImages() {
        LaunchNoteDatabase.getDatabase(this)?.let {
            it.picNoteDao().loadAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { dbPicNotes ->
                        val loadedPicNotes = picNotes.isNotEmpty()
                        // clear the currently saved picNotes
                        // and replace them with new ones from the database
                        picNotes.clear()
                        picNotes.addAll(dbPicNotes)

                        adapter.notifyDataSetChanged()

                        if (!loadedPicNotes) {
                            val index = picNotes.indexOfFirst { it.id == defaultImageId }
                            viewPager.currentItem = index
                        }
                    }
        }
    }

    /**
     * Called when toolbar's "delete" is pressed
     */
    private fun onDeletePressed() {
        // get the id of the current item displayed in the ViewPager and delete it
        LaunchNoteDatabase
                .getDatabase(this)
                ?.picNoteDao()
                ?.findById(picNotes[viewPager.currentItem].id)
                ?.map { picNotes ->
                    picNotes.forEach {
                        val success = PicNote.deleteFromDb(this, it)
                        if (!success) Toast.makeText(this, "Cannot delete $defaultImageId", Toast.LENGTH_LONG).show()
                    }
                }?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe()
        // exit full screen
        onBackPressed()
    }

    private fun onEditPressed() {
        val i = Intent(this, PhotoInfoActivity::class.java)
        i.putExtra(PhotoInfoActivity.PIC_NOTE_ARG, picNotes[viewPager.currentItem])
        startActivity(i)
    }

    internal inner class PhotoViewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
        override fun getCount(): Int = picNotes.size

        override fun getItem(position: Int): Fragment = PhotoViewFragment.newInstance(picNotes[position])
    }

    private fun isImmersiveModeEnabled(): Boolean = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions

    companion object {
        const val EXTRA_INTENT_IMAGE_URI = "INTENT_IMAGE_URI"
        const val EXTRA_INTENT_IMAGE_ID = "INTENT_IMAGE_POSITION"
    }
}
