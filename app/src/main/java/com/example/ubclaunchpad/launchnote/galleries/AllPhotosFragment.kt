package com.example.ubclaunchpad.launchnote.galleries

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.database.PicNoteDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class AllPhotosFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_all_photos, null)
    }

    override fun onResume() {
        super.onResume()
        loadImages()
    }

    private fun loadImages() {
        PicNoteDatabase.getDatabase(activity)?.let {
            it.picNoteDao().loadAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { picNotes ->
                        for (next in picNotes) {
                            // TODO: display it in RecyclerView
                            Toast.makeText(activity, "${next.id} ${next.imageUri}", Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }
}