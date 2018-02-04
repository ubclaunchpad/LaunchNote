package com.example.ubclaunchpad.launchnote.toolbar

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.ubclaunchpad.launchnote.R
import java.io.Serializable
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PhotoNavigatonToolbarFragment.OnButtonPressListener] interface
 * to handle interaction events.
 * Use the [PhotoNavigatonToolbarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoNavigatonToolbarFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mMode: ToolbarMode = ToolbarMode.NormalMode
    private var mListener: OnButtonPressListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mMode = arguments.getSerializable(MODE) as ToolbarMode
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val inflatedView =  inflater!!.inflate(R.layout.photo_navigation_bar, container, false)
        setMode(mMode, inflatedView)
        return inflatedView
    }

    fun setMode(newMode: ToolbarMode, view: View = getView()!!) {
        mMode.buttons.forEach {
            val v = view.findViewById<View>(it.id)
            v.post {
                v.visibility = View.GONE
                it.listeners.forEach {
                    view.findViewById<View>(it).setOnClickListener {  }
                }
            }
        }
        newMode.buttons.forEach{
            val v = view.findViewById<View>(it.id)
            v.post {
                v.visibility = View.VISIBLE
                it.listeners.forEach { listenerId ->
                    view.findViewById<View>(listenerId).setOnClickListener {
                        mListener!!.onButtonClicked(listenerId)
                    }
                }
            }

        }
        mMode = newMode

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnButtonPressListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnButtonPressListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
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
        // TODO: Update argument type and name
        fun onButtonClicked(butonInfo: Int)
    }

    companion object {
        private val BUTTONS = "BUTTONS"
        private val MODE = "MODE"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param mode Parameter 2.
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

    sealed class ToolbarMode(val buttons: ArrayList<ElementInfo>) : Serializable {
        object EditMode : ToolbarMode(ArrayList(editButtons.toList()))
        object NormalMode: ToolbarMode(ArrayList(normalModeButtons.toList()))

        companion object {
            val editButtonsListeners = arrayOf(R.id.edit_toolbar_back_btn, R.id.edit_toolbar_delete_btn)
            val editButtons = arrayOf(ElementInfo(R.id.edit_toolbar_layout, ArrayList(editButtonsListeners.toList())))
            val normalModeButtons = arrayOf<ElementInfo>()
        }
    }

    data class ElementInfo(val id: Int, val listeners: ArrayList<Int> = ArrayList())
}// Required empty public constructor
