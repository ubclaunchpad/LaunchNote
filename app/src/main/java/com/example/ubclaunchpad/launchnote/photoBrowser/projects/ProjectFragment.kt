package com.example.ubclaunchpad.launchnote.photoBrowser.projects

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import com.example.ubclaunchpad.launchnote.models.Project
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by sherryuan on 2018-01-09.
 */

/**
 * Fragment displaying all photos in a grid format
 */
class ProjectFragment : Fragment() {

    // a MutableList in Kotlin is the same as a List in Java
    // Kotlin also has a List class, but it's immutable and doesn't let you add/remove items
    private var projects: MutableList<Project> = mutableListOf()

    lateinit var adapter: ProjectVerticalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadImages()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_project, null)
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
        projects.clear()
        LaunchNoteDatabase.getDatabase(activity)?.let { database ->
            database.projectDao().loadAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { dbProjects ->
                        // loop through all the projects
                        dbProjects.forEach { dbProject ->
                            val numPicNotes = AtomicInteger(0)
                            dbProject.picNoteIds.forEach {
                                // for each project, loop through the picNoteIds
                                database.picNoteDao().findById(it).subscribe {
                                    // get the actual PicNote based on the id and add to the project's PicNotes
                                    dbProject.picNotes.addAll(it)
                                    if (numPicNotes.incrementAndGet() == dbProject.picNoteIds.size) {
                                        // if we've added all the PicNotes for the project, add project to projects
                                        projects.add(dbProject)
                                        adapter.setProjects(projects)
                                        Toast.makeText(activity, "updating adapter", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
        }
    }

    private fun initViews(numColumn: Int) {
        // do nothing
    }

    companion object {
        // declaring a const val in a Kotlin class's companion object is equivalent to
        // declaring a static final variable in Java
        // Kotlin doesn't have the static keyword, so companion objects are where you put static things
        const val NUM_COLUMNS_PORTRAIT = 3
        const val NUM_COLUMNS_LANDSCAPE = 4
    }
}