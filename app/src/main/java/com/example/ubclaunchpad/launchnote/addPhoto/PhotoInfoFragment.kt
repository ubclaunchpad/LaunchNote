package com.example.ubclaunchpad.launchnote.addPhoto

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.database.PicNoteDatabase
import com.example.ubclaunchpad.launchnote.models.PicNote
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_photo_info.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PhotoInfoFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PhotoInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoInfoFragment : Fragment() {

    private var picNoteToSave: PicNote? = null

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //val uriStr = arguments.getString(TakePhotoActivity.URI_KEY)
        //imageURI = Uri.parse(uriStr)
        save_button.setOnClickListener { saveImgToDB() }
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PhotoInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): PhotoInfoFragment {
            val fragment = PhotoInfoFragment()
            val args = Bundle()
            //args.putString(ARG_PARAM1, param1)
            //args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
