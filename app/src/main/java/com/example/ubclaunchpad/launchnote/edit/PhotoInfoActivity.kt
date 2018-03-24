package com.example.ubclaunchpad.launchnote.edit

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import com.example.ubclaunchpad.launchnote.models.PicNote
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_photo_info.*

class PhotoInfoActivity : AppCompatActivity() {

    private lateinit var picNoteToEdit: PicNote
    private var removeIfNoSave: Boolean = false


    private lateinit var imageHolder: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_info)
        ButterKnife.bind(this)

        picNoteToEdit = intent.extras.getSerializable(PIC_NOTE_ARG) as PicNote
        removeIfNoSave = intent.extras.containsKey(REMOVE_IMAGES_IF_NO_CHANGE) &&
                intent.extras.getBoolean(REMOVE_IMAGES_IF_NO_CHANGE)

        viewManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        viewAdapter = ClassViewAdapter(arrayOf("CPSC 213", "CPSC 313", "CPSC 406", "CPSC 213", "CPSC 313", "CPSC 406", "CPSC 213", "CPSC 313", "CPSC 406", "CPSC 213", "CPSC 313", "CPSC 406", "CPSC 213", "CPSC 313", "CPSC 406"))

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view_select_list).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = viewManager
            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        imageHolder = findViewById<ImageView>(R.id.info_panel_image_view).apply {
            setImageURI(Uri.parse(picNoteToEdit.compressedImageUri))
        }


        if(picNoteToEdit.title.isNotEmpty())
            title_input.setText(picNoteToEdit.title)
        if(picNoteToEdit.description.isNotEmpty())
            description_input.setText(picNoteToEdit.description)
        // todo vpineda we need to create a dropdown for all of the classes or projects!
        if(picNoteToEdit.folderId != PicNote.DEFAULT_FOLDERID)
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

    class ClassViewAdapter(private val myDataset: Array<String>) :
            RecyclerView.Adapter<ClassViewAdapter.ViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val textView = view.findViewById<TextView>(R.id.class_button_info_textview)
        }


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ClassViewAdapter.ViewHolder {
            // create a new view
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.class_button_layout, parent, false)
            // set the view's size, margins, paddings and layout parameters
            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Log.i("TEST", "Uhm " + position)
            holder.textView.text = myDataset[position]
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }

    companion object {
        const val REMOVE_IMAGES_IF_NO_CHANGE = "REMOVE_FROM_DB_IF_BACK"
        const val PIC_NOTE_ARG = "PICNOTE"
    }
}
