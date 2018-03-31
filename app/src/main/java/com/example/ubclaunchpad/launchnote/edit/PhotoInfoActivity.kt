package com.example.ubclaunchpad.launchnote.edit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import com.example.ubclaunchpad.launchnote.models.Folder
import com.example.ubclaunchpad.launchnote.models.PicNote
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_photo_info.*

class PhotoInfoActivity : AppCompatActivity() {

    private lateinit var picNoteToEdit: PicNote
    private var removeIfNoSave: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_info)
        ButterKnife.bind(this)

        picNoteToEdit = intent.extras.getSerializable(PIC_NOTE_ARG) as PicNote
        removeIfNoSave = intent.extras.containsKey(REMOVE_IMAGES_IF_NO_CHANGE) &&
                intent.extras.getBoolean(REMOVE_IMAGES_IF_NO_CHANGE)

        if(picNoteToEdit.title.isNotEmpty())
            title_input.setText(picNoteToEdit.title)
        if(picNoteToEdit.description.isNotEmpty())
            description_input.setText(picNoteToEdit.description)
        // todo vpineda we need to create a dropdown for all of the classes or projects!
        if(picNoteToEdit.folderId != Folder.DEFAULT_FOLDERID)
            folder_input.setText(picNoteToEdit.folderId.toString())
    }

    @OnClick(R.id.save_button)
    fun onSaveButtonPressed(view: View) {

        picNoteToEdit.title = title_input.text.toString()
        picNoteToEdit.description = description_input.text.toString()
        // todo vpineda we need to create a dropdown for all of the classes or projects!
        if(folder_input.text.isNotEmpty())
            picNoteToEdit.folderId = Integer.parseInt(folder_input.text.toString())

        // insert image into database on a different thread
        LaunchNoteDatabase.getDatabase(this)?.let {
            Observable.fromCallable {
                it.picNoteDao().insert(picNoteToEdit) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        // todo vpineda HACK, this might come back and bite me in the ass :(
                        removeIfNoSave = false
                        finish()
                    }
        }
    }

    override fun onDestroy() {
        if(removeIfNoSave) {
            LaunchNoteDatabase.getDatabase(this)?.let {
                Observable.fromCallable {
                    PicNote.deleteFromDb(this, picNoteToEdit)
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
            }
        }
        super.onDestroy()
    }

    companion object {
        const val REMOVE_IMAGES_IF_NO_CHANGE = "REMOVE_FROM_DB_IF_BACK"
        const val PIC_NOTE_ARG = "PICNOTE"
    }
}
