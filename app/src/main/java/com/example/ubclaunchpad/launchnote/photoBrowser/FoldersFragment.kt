package com.example.ubclaunchpad.launchnote.photoBrowser

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import com.example.ubclaunchpad.launchnote.models.Folder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by sherryuan on 2018-03-24.
 */

class FoldersFragment : Fragment() {

    // a MutableList in Kotlin is the same as a List in Java
    // Kotlin also has a List class, but it's immutable and doesn't let you add/remove items
    private val folders: MutableList<Folder> = mutableListOf()

    private lateinit var adapter: FoldersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_folder, null)
    }

    override fun onResume() {
        super.onResume()
        initViews()
        loadFolders()
    }

    private fun initViews() {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.folder_recycler_view)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        adapter = FoldersAdapter(activity, folders)
        recyclerView.adapter = adapter
    }

    private fun loadFolders() {
        LaunchNoteDatabase.getDatabase(activity)?.let {
            it.folderDao().loadAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { loadedFolders ->
                        // clear the currently saved folders
                        // and replace them with new ones from the database
                        folders.clear()
                        folders += loadedFolders
                        adapter.notifyDataSetChanged()
                    }
        }
    }
}