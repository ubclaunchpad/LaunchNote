package com.example.ubclaunchpad.launchnote.photoBrowser

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.models.PicNote
import com.example.ubclaunchpad.launchnote.toolbar.PhotoNavigatonToolbarFragment
import com.github.chrisbanes.photoview.PhotoView

/**
 * A simple [Fragment] subclass.
 * Use the [PhotoViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var picNoteId: Int = -1
    private var uri: String = ""
    private lateinit var toolbarFragment: PhotoNavigatonToolbarFragment
    private var photoView: PhotoView? = null
    private var isActiveEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            picNoteId = arguments.getInt(ARG_ITEM_ID)
            uri = arguments.getString(ARG_ITEM_URI)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_picnote, container, false)
        photoView = view?.findViewById(R.id.photo_view)

        setUpToolbar()

        setImageIn(uri, photoView)
        return view
    }

    private fun setImageIn(uri: String, dest: PhotoView?) {
        Glide.with(this)
                .asBitmap()
                .load(uri)
                .into<SimpleTarget<Bitmap>>(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                        // have photoView display the loaded Bitmap
                        dest?.setImageBitmap(resource)
                    }
                })
    }

    /**
     * Set up the toolbar for editing photos
     */
    private fun setUpToolbar() {
        toolbarFragment = activity.supportFragmentManager.findFragmentById(R.id.expand_photo_toolbar_fragment) as PhotoNavigatonToolbarFragment

        // hide the toolbar when image is first loaded
        activity.supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.tooltip_enter, R.anim.tooltip_exit)
                .hide(toolbarFragment)
                .commit()

        photoView?.setOnClickListener {
            isActiveEdit = !isActiveEdit
            if (isActiveEdit) {
                // if it's an active edit, show toolbar
                activity.supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.tooltip_enter, R.anim.tooltip_exit)
                        .show(toolbarFragment)
                        .commit()
            } else {
                // if it's not an active edit, hide toolbar
                activity.supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.tooltip_enter, R.anim.tooltip_exit)
                        .hide(toolbarFragment)
                        .commit()
            }
        }
    }

    companion object {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_ITEM_ID = "id"
        private const val ARG_ITEM_URI = "uri"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param picNote pic note reference
         * @return A new instance of fragment PhotoViewFragment.
         */
        fun newInstance(picNote: PicNote): PhotoViewFragment {
            val fragment = PhotoViewFragment()
            val args = Bundle()
            args.putInt(ARG_ITEM_ID, picNote.id)
            args.putString(ARG_ITEM_URI, picNote.imageUri)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
