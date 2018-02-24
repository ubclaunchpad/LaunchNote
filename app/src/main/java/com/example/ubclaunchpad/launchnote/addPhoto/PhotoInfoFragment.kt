package com.example.ubclaunchpad.launchnote.addPhoto

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.models.PicNote
import kotlinx.android.synthetic.main.fragment_photo_info.*

/**
 * A simple fragment that takes in a serialized
 */
class PhotoInfoFragment : Fragment() {

    private lateinit var picNoteToEdit: PicNote
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        picNoteToEdit = savedInstanceState?.getSerializable(PICNOTE) as PicNote
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_photo_info, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    @OnClick(R.id.save_button)
    private fun saveImgToDB() {
        val uriStr = arguments.getString(TakePhotoActivity.URI_KEY)
        val imageURI = Uri.parse((uriStr))
        Log.d("title", title_input.text.toString())
        Log.d("description", description_input.text.toString())
        Log.d("class", class_input.text.toString())
        Log.d("project", project_input.text.toString())

        /*
        // TODO: parse out the description
        // passing in empty string for now
        Log.d("TEST", "testing")
        picNoteToSave = PicNote(imageURI.toString(), "", "")

        // insert image into database on a different thread
        PicNoteDatabase.getDatabase(activity)?.let {
            Observable.fromCallable { it.picNoteDao().insert(picNoteToSave) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }*/
    }

    /**
     *
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val PICNOTE = "PICNOTE"

        /**
         * @param picNote picnote
         * @return A new instance of fragment PhotoInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(picNote: PicNote): PhotoInfoFragment {
            val fragment = PhotoInfoFragment()
            val args = Bundle()
            args.putSerializable(PICNOTE, picNote)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
