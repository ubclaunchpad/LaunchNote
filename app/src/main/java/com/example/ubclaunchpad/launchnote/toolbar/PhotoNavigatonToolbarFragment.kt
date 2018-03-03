package com.example.ubclaunchpad.launchnote.toolbar

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ubclaunchpad.launchnote.R
import java.io.Serializable


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PhotoNavigatonToolbarFragment.OnButtonPressListener] interface
 * to handle interaction events.
 * Use the [PhotoNavigatonToolbarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoNavigatonToolbarFragment : Fragment() {

    private var toolMode: ToolbarMode = ToolbarMode.NormalMode
    private var buttonListener: OnButtonPressListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            toolMode = it.getSerializable(MODE) as ToolbarMode
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val inflatedView =  inflater.inflate(R.layout.photo_navigation_bar, container, false)
        setMode(toolMode, inflatedView)
        return inflatedView
    }

    fun setMode(newMode: ToolbarMode, view: View = getView()!!) {
        toolMode.elements.forEach {
            val v = view.findViewById<View>(it.id)
            v.visibility = View.GONE
            it.listeners.forEach {
                view.findViewById<View>(it).setOnClickListener {  }
            }
        }
        newMode.elements.forEach{ elementInfo ->
            val v = view.findViewById<View>(elementInfo.id)
            v.visibility = View.VISIBLE
            elementInfo.listeners.forEach { listenerId ->
                view.findViewById<View>(listenerId).setOnClickListener {
                    buttonListener?.onButtonClicked(listenerId)
                }
            }

        }
        toolMode = newMode
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnButtonPressListener) {
            buttonListener = context
        } else {
            throw RuntimeException("$context must implement OnButtonPressListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        buttonListener = null
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
    interface OnButtonPressListener {
        fun onButtonClicked(butonInfo: Int)
    }

    companion object {
        private val MODE = "MODE"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param mode the mode the fragment will be created.
         * @return A new instance of fragment PhotoNavigatonToolbarFragment.
         */
        fun newInstance(mode: ToolbarMode): PhotoNavigatonToolbarFragment {
            val fragment = PhotoNavigatonToolbarFragment()
            val args = Bundle()
            args.putSerializable(MODE, mode)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * Used to indicate the current state of the toolbar. Since its sealed only the objects declared
     * here will be possible toolbar states
     * @param elements a list of elements that will be displayed with the list of inner elements
     *               that we want to set a click listener to
     */
    sealed class ToolbarMode(val elements: ArrayList<ElementInfo>) : Serializable {
        object EditMode : ToolbarMode(ArrayList(editButtons.toList()))
        object NormalMode: ToolbarMode(ArrayList(normalModeButtons.toList()))

        companion object {
            val editButtonsListeners = arrayOf(R.id.edit_toolbar_back_btn, R.id.edit_toolbar_delete_btn, R.id.edit_toolbar_text_view, R.id.edit_toolbar_edit_desc_btn)
            val editButtons = arrayOf(ElementInfo(R.id.edit_toolbar_layout, ArrayList(editButtonsListeners.toList())))
            val normalModeButtons = arrayOf<ElementInfo>(ElementInfo(R.id.photo_view_title))
        }
    }

    /**
     * @param id parent id of the element that you want to show (ie LinearLayout)
     * @param listeners elements inside of the parent id that will invoke this fragment's listener
     */
    data class ElementInfo(val id: Int, val listeners: ArrayList<Int> = ArrayList())
}// Required empty public constructor
