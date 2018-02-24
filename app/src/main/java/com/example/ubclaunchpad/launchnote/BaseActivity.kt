package com.example.ubclaunchpad.launchnote

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.PopupMenu
import com.example.ubclaunchpad.launchnote.addPhoto.GalleryActivity
import com.example.ubclaunchpad.launchnote.addPhoto.PhotoInfoFragment
import com.example.ubclaunchpad.launchnote.addPhoto.TakePhotoActivity
import com.example.ubclaunchpad.launchnote.models.PicNote
import com.example.ubclaunchpad.launchnote.photoBrowser.PhotoBrowserActivity

/**
 * A base Activity that will set up the bottom navigation bar for any Activities extending it
 */
abstract class BaseActivity : AppCompatActivity() {

    internal lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentViewId())
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        // set up the elements in the bottom navigation bar
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.add_menu_item -> {
                    onAddPhotoClick()
                }
                R.id.scan_menu_item -> {
                    // TODO: create an Activity to handle scanning and move bottom nav selection there
                    bottomNavigation.menu.getItem(SCAN_MENU_ITEM).isChecked = true
                }
                R.id.browse_menu_item -> {
                    val photoBrowserActivityIntent = Intent(applicationContext, PhotoBrowserActivity::class.java)
                            .apply { flags = FLAG_ACTIVITY_REORDER_TO_FRONT }
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
    private fun onAddPhotoClick() =
            PopupMenu(applicationContext, bottomNavigation).apply {
                //Inflating the Popup using xml file
                menuInflater.inflate(R.menu.add_photo, this.menu)

                //registering popup with OnMenuItemClickListener
                setOnMenuItemClickListener { item ->
                    if (item.itemId == R.id.take_photo_item) {
                        val b = Bundle()
                        val takePhotoActivityIntent = Intent(applicationContext, TakePhotoActivity::class.java)
                                .apply { flags = FLAG_ACTIVITY_REORDER_TO_FRONT }
                        startActivityForResult(takePhotoActivityIntent, TakePhotoActivity.TAKE_PHOTO_REQUEST_CODE)
                    } else if (item.itemId == R.id.add_from_library_item) {
                        val galleryActivityIntent = Intent(applicationContext, GalleryActivity::class.java)
                                .apply { flags = FLAG_ACTIVITY_REORDER_TO_FRONT }
                        startActivityForResult(galleryActivityIntent, GalleryActivity.GALLERY_ACTIVITY_REQ_CODE)
                    }
                    true
                }
                show()
            }

    abstract fun getContentViewId(): Int

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == TakePhotoActivity.TAKE_PHOTO_REQUEST_CODE || requestCode == GalleryActivity.GALLERY_ACTIVITY_REQ_CODE) {
            when (resultCode) {
                PHOTO_SAVED -> {
                    val picNote = data!!.getSerializableExtra(PIC_NOTE_KEY) as PicNote
                    editPicNote(picNote)
                }
                PHOTO_NOT_SAVED -> {
                    // todo vpineda what to do when the photo is not saved
                }
            }
        }
    }

    private fun editPicNote(pn: PicNote) {
        // Begin transaction
        val transaction = supportFragmentManager.beginTransaction()
        // Replace the contents of the container with the info fragment
        val fragment = PhotoInfoFragment.newInstance(pn)
        transaction.replace(R.id.photo_browser_container, fragment)
        transaction.addToBackStack("photo_info_fragment")
        // or transaction.replace(R.id.take_photo_container, PhotoInfoFragment())
        // Complete the changes added above
        transaction.commit()
    }

    companion object {
        const val ADD_MENU_ITEM = 0
        const val SCAN_MENU_ITEM = 1
        const val BROWSE_MENU_ITEM = 2

        const val PHOTO_SAVED = 21421
        const val PHOTO_NOT_SAVED = 45346
        const val PIC_NOTE_KEY = "PIC_NOTE_KEY"
    }
}