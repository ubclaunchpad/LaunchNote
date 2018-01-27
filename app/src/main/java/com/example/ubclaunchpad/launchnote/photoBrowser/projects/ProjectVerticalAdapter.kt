package com.example.ubclaunchpad.launchnote.photoBrowser.projects

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.models.PicNote
import com.example.ubclaunchpad.launchnote.models.Project


/**
 * Created by sherryuan on 2018-01-27.
 */
class ProjectVerticalAdapter() : RecyclerView.Adapter<ProjectVerticalAdapter.ViewHolder>() {

    private lateinit var projects: List<Project>
    private lateinit var context: Context

    constructor(context: Context, projects: List<Project>) : this() {
        this.projects = projects
        this.context = context
    }

    fun setProjects(newProjects: List<Project>) {
        if (projects !== newProjects) {
            projects = newProjects
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_project_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = projects[position].description
        holder.horizontalAdapter.setPicNotes(projects[position].picNotes) // List of PicNotes
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById<View>(R.id.project_name) as TextView
        val horizontalList: RecyclerView = view.findViewById<View>(R.id.horizontal_list) as RecyclerView
        val horizontalAdapter = ProjectHorizontalAdapter()

        init {
            horizontalList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            horizontalList.adapter = horizontalAdapter
        }
    }
}