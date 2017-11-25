package com.example.ubclaunchpad.launchnote.photoBrowser

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import com.example.ubclaunchpad.launchnote.models.PicNote
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Fragment displaying all photos in a grid format
 */
class AllPhotosFragment : Fragment() {

    // a MutableList in Kotlin is the same as a List in Java
    // Kotlin also has a List class, but it's immutable and doesn't let you add/remove items
    private var picNotes: MutableList<PicNote> = mutableListOf()

    lateinit var adapter: AllPhotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadImages()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_all_photos, null)
    }

    override fun onResume() {
        super.onResume()
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            initViews(NUM_COLUMNS_PORTRAIT) // 3 columns while straight
        } else {
            initViews(NUM_COLUMNS_LANDSCAPE) // 4 columns while rotated
        }
    }

    private fun loadImages() {
        LaunchNoteDatabase.getDatabase(activity)?.let {
            it.picNoteDao().loadAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { dbPicNotes ->
                        // clear the currently saved picNotes
                        // and replace them with new ones from the database
                        picNotes.clear()
                        for (next in dbPicNotes) {
                            picNotes.add(next)
                            adapter.notifyDataSetChanged()
                        }
                    }
        }
    }

    private fun initViews(numColumn: Int) {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.card_recycler_view)
        recyclerView.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(activity, numColumn)
        recyclerView.layoutManager = layoutManager

        adapter = AllPhotosAdapter(activity, picNotes)
        recyclerView.adapter = adapter
    }

    companion object {
        // declaring a const val in a Kotlin class's companion object is equivalent to
        // declaring a static final variable in Java
        // Kotlin doesn't have the static keyword, so companion objects are where you put static things
        const val NUM_COLUMNS_PORTRAIT = 3
        const val NUM_COLUMNS_LANDSCAPE = 4
    }
}