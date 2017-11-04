package com.example.ubclaunchpad.launchnote

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.PopupMenu
import com.example.ubclaunchpad.launchnote.addPhoto.GalleryActivity
import com.example.ubclaunchpad.launchnote.addPhoto.TakePhotoActivity
import com.example.ubclaunchpad.launchnote.photoBrowser.PhotoBrowserActivity


abstract class BaseActivity : AppCompatActivity() {

    lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentViewId())

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.add_menu_item -> {
                    onAddPhotoClick()
                }
                R.id.scan_menu_item -> {

                }
                R.id.browse_menu_item -> {
                    val photoBrowserActivityIntent = Intent(applicationContext, PhotoBrowserActivity::class.java)
                            .apply { flags = FLAG_ACTIVITY_NEW_TASK }
                    startActivity(photoBrowserActivityIntent)
                }
            }
            true
        }
    }

    /**
     * When "Add Photo" is clicked, show menu allowing users to choose between
     * uploading from gallery and taking a photo
     */
    fun onAddPhotoClick() =
            PopupMenu(applicationContext, bottomNavigation).apply {
                //Inflating the Popup using xml file
                menuInflater.inflate(R.menu.add_photo, this.menu)

                //registering popup with OnMenuItemClickListener
                setOnMenuItemClickListener { item ->
                    if (item.itemId == R.id.take_photo_item) {
                        val takePhotoActivityIntent = Intent(applicationContext, TakePhotoActivity::class.java)
                                .apply { flags = FLAG_ACTIVITY_NEW_TASK }
                        startActivity(takePhotoActivityIntent)
                    } else if (item.itemId == R.id.add_from_library_item) {
                        val galleryActivityIntent = Intent(applicationContext, GalleryActivity::class.java)
                                .apply { flags = FLAG_ACTIVITY_NEW_TASK }
                        startActivity(galleryActivityIntent)
                    }
                    true
                }
                show()
            }

    abstract fun getContentViewId(): Int
}