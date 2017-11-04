package com.example.ubclaunchpad.launchnote

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.example.ubclaunchpad.launchnote.database.PicNoteDatabase
import com.example.ubclaunchpad.launchnote.gallery.GalleryActivity

import butterknife.ButterKnife
import butterknife.OnClick
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        loadImages()
    }

    /**
     * load all images from database
     */
    private fun loadImages() {
        PicNoteDatabase.getDatabase(this)?.let {
            it.picNoteDao().loadAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { picNotes ->
                        for (next in picNotes) {
                            // TODO: display it in RecyclerView
                            Toast.makeText(this, "${next.id} ${next.imageUri}", Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    @OnClick(R.id.buttonUploadFromGallery)
    fun launchGalleryActivity(view: View) =
            startActivity(Intent(this, GalleryActivity::class.java))

    @OnClick(R.id.buttonTakePhoto)
    fun launchTakePhotoActivity(view: View) =
            startActivity(Intent(this, TakePhotoActivity::class.java))
}
