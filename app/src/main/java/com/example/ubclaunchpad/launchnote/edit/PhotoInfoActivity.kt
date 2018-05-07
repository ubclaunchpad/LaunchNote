package com.example.ubclaunchpad.launchnote.edit

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    // todo vpineda this should be a set of something else

    private lateinit var imageHolder: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var classButtons = arrayOf("CPSC 213", "CPSC 313", "CPSC 406",
            "CPSC 213", "CPSC 313", "CPSC 406", "CPSC 213",
            "CPSC 313", "CPSC 406", "CPSC 213", "CPSC 313",
            "CPSC 406", "CPSC 213", "CPSC 313", "CPSC 406").map { ClassButtonModel(it) }.toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_info)
        ButterKnife.bind(this)

        savedInstanceState?.let {
            classButtons = it.getParcelableArray(LIST_OF_CLASSES_KEY) as Array<ClassButtonModel>
        }

        picNoteToEdit = intent.extras.getSerializable(PIC_NOTE_ARG) as PicNote
        removeIfNoSave = intent.extras.containsKey(REMOVE_IMAGES_IF_NO_CHANGE) &&
                intent.extras.getBoolean(REMOVE_IMAGES_IF_NO_CHANGE)

        viewManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        viewAdapter = ClassViewAdapter(classButtons)

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

        findViewById<ImageButton>(R.id.edit_view_back_button).setOnClickListener {
            finish()
        }

        if (picNoteToEdit.title.isNotEmpty())
            title_input.setText(picNoteToEdit.title)
        if (picNoteToEdit.description.isNotEmpty())
            description_input.setText(picNoteToEdit.description)
        // todo vpineda we need to create a dropdown for all of the classes or projects!
        if (picNoteToEdit.folderId != Folder.DEFAULT_FOLDERID)
            folder_input.setText(picNoteToEdit.folderId.toString())
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putParcelableArray(LIST_OF_CLASSES_KEY, classButtons)
    }

    @OnClick(R.id.save_button)
    fun onSaveButtonPressed(view: View) {

        picNoteToEdit.title = title_input.text.toString()
        picNoteToEdit.description = description_input.text.toString()
        // todo vpineda we need to create a dropdown for all of the classes or projects!
        if (folder_input.text.isNotEmpty()) {
            picNoteToEdit.folderId = Integer.parseInt(folder_input.text.toString())

            // create new Folder
            LaunchNoteDatabase.getDatabase(this)?.let {
                it.folderDao().findById(folder_input.text.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { loadedFolder ->
                            // add picNoteToEdit to the folder with matching folderId
                            addPicNoteToFolder(loadedFolder)
                        }
            }
        }

        // insert image into database on a different thread
        LaunchNoteDatabase.getDatabase(this)?.let {
            Observable.fromCallable {
                it.picNoteDao().insert(picNoteToEdit)
            }
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
        if (removeIfNoSave) {
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

    // add picNoteToEdit to the loadedFolder
    // if folder doesn't already exist in the database, create a new one
    private fun addPicNoteToFolder(loadedFolder: List<Folder>) {

        var folderToInsert = Folder()

        if (loadedFolder.isEmpty()) {
            // if there isn't already a folder with that ID, create a new one
            folderToInsert.id = picNoteToEdit.folderId
            // TODO: let users pick a name
            folderToInsert.name = picNoteToEdit.folderId.toString()
            folderToInsert.picNoteIds.add(picNoteToEdit.id)

            // insert folder into database on a different thread
            LaunchNoteDatabase.getDatabase(this)?.let {
                Observable.fromCallable {
                    it.folderDao().insert(folderToInsert)
                }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
            }

        } else {
            // folder with that ID already exists
            // so add picNoteToEdit's id to existing folder's list of IDs
            folderToInsert = loadedFolder[0]
            if (!folderToInsert.picNoteIds.contains(picNoteToEdit.id)) {
                folderToInsert.picNoteIds.add(picNoteToEdit.id)

                // insert folder into database on a different thread
                LaunchNoteDatabase.getDatabase(this)?.let {
                    Observable.fromCallable {
                        it.folderDao().insert(folderToInsert)
                    }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                }
            }
        }
    }

    class ClassViewAdapter(private val myDataset: Array<ClassButtonModel>) :
            RecyclerView.Adapter<ClassViewAdapter.ViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val button = view.findViewById<ToggleButton>(R.id.class_button_info_textview)
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
            holder.button.textOn = myDataset[position].name
            holder.button.textOff = myDataset[position].name
            holder.button.text = myDataset[position].name

            holder.button.setOnCheckedChangeListener { btn: CompoundButton?, isChecked: Boolean ->
                myDataset[position].active = isChecked
            }
            holder.button.isChecked = myDataset[position].active
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }

    data class ClassButtonModel(val name: String, var active: Boolean = false) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readByte() != 0.toByte())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeByte(if (active) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ClassButtonModel> {
            override fun createFromParcel(parcel: Parcel): ClassButtonModel {
                return ClassButtonModel(parcel)
            }

            override fun newArray(size: Int): Array<ClassButtonModel?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        const val REMOVE_IMAGES_IF_NO_CHANGE = "REMOVE_FROM_DB_IF_BACK"
        const val PIC_NOTE_ARG = "PICNOTE"

        internal const val LIST_OF_CLASSES_KEY = "CLASS_LIST_KEY"
    }
}
