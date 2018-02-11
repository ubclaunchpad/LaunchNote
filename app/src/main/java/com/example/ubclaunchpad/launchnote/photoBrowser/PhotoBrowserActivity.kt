package com.example.ubclaunchpad.launchnote.photoBrowser

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.ViewGroup
import butterknife.ButterKnife
import com.example.ubclaunchpad.launchnote.BaseActivity
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.models.PicNote
import com.example.ubclaunchpad.launchnote.toolbar.PhotoNavigatonToolbarFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_photo_browser.*
import kotlinx.android.synthetic.main.photo_navigation_bar.*

/**
 * Activity for browsing photos in different folders
 */
class PhotoBrowserActivity : BaseActivity(), AllPhotosFragment.OnEditPhotoMode, PhotoNavigatonToolbarFragment.OnButtonPressListener {
    lateinit var customPagerAdapter: CustomPagerAdapter
    lateinit var toolbarFragment: PhotoNavigatonToolbarFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        // set up the toolbar
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        setSupportActionBar(topToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // set up ViewPager
        // this lets us swipe between the three different ways of browsing photos:
        // by LaunchNoteClass, by Project, or by All photos
        // each of them is represented by a fragment
        customPagerAdapter = CustomPagerAdapter(supportFragmentManager, this)
        toolbarFragment = supportFragmentManager.findFragmentById(R.id.photoBrowserToolbarFragment) as PhotoNavigatonToolbarFragment

        view_pager.adapter = customPagerAdapter
        // todo vpineda should we be reading the current item from the bundle rather than hardcoding it?
        view_pager.currentItem = ALL_FRAGMENT
        // add listener to view_pager that will update elements when user scrolls to new page
        view_pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                // do nothing
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // do nothing
            }

            override fun onPageSelected(position: Int) {
                // when new page is selected, update button
                updateButtons(position)
            }

        })
        updateButtons(ALL_FRAGMENT)

        // Watch for button clicks. When a button is clicked, go to the correct fragment
        class_button.setOnClickListener {
            view_pager.currentItem = CLASS_FRAGMENT
            updateButtons(CLASS_FRAGMENT)

        }
        project_button.setOnClickListener {
            view_pager.currentItem = PROJECT_FRAGMENT
            updateButtons(PROJECT_FRAGMENT)
        }
        all_button.setOnClickListener {
            view_pager.currentItem = ALL_FRAGMENT
            updateButtons(ALL_FRAGMENT)
        }

        // Restore state of fragments
        (0 until NUMBER_OF_FRAGMENTS)
                .map { // todo vpineda generalize to the diff types of photo fragments
                    supportFragmentManager.findFragmentByTag("android:switcher:${R.id.view_pager}:$it") as AllPhotosFragment?
                }
                .forEach { f ->
                    f?.let {
                        it.onListener = this
                    }
                }
    }

    override fun onEditPhotoMode(isActiveEdit: Boolean, selectedImages: Set<PicNote>) {
        Observable.just(isActiveEdit)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it) {
                        toolbarFragment.setMode(PhotoNavigatonToolbarFragment.ToolbarMode.EditMode)
                    } else {
                        toolbarFragment.setMode(PhotoNavigatonToolbarFragment.ToolbarMode.NormalMode)
                    }
                }
    }

    override fun onButtonClicked(butonInfo: Int) {
        Log.i("INFO","Clicked " + butonInfo)
        when(butonInfo) {
            R.id.edit_toolbar_back_btn -> {
                customPagerAdapter.currentFragment!!.cancelSelection()
            }
            R.id.edit_toolbar_text_view -> {
                customPagerAdapter.currentFragment!!.cancelSelection()
            }
            R.id.edit_toolbar_delete_btn -> {
                customPagerAdapter.currentFragment!!.removeSelection()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.getItem(BROWSE_MENU_ITEM).isChecked = true
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_photo_browser
    }

    private fun updateButtons(fragmentId: Int) {
        class_button.setTextColor(resources.getColor(R.color.darkGreyText))
        project_button.setTextColor(resources.getColor(R.color.darkGreyText))
        all_button.setTextColor(resources.getColor(R.color.darkGreyText))

        when (fragmentId) {
            CLASS_FRAGMENT -> class_button.setTextColor(resources.getColor(R.color.colorAccent))
            PROJECT_FRAGMENT -> project_button.setTextColor(resources.getColor(R.color.colorAccent))
            ALL_FRAGMENT -> all_button.setTextColor(resources.getColor(R.color.colorAccent))
        }
    }

    /**
     * FragmentPagerAdapter class
     */
    class CustomPagerAdapter(fragmentManager: FragmentManager, val editModeListener: AllPhotosFragment.OnEditPhotoMode) : FragmentPagerAdapter(fragmentManager) {
        val NUM_ITEMS = 3
        var currentFragment : AllPhotosFragment? = null

        override fun getCount(): Int {
            return NUM_ITEMS
        }

        override fun setPrimaryItem(container: ViewGroup?, position: Int, `object`: Any?) {
            super.setPrimaryItem(container, position, `object`)

            if(currentFragment != `object`) {
                currentFragment?.cancelSelection()
                currentFragment = `object` as AllPhotosFragment
            }
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
            super.restoreState(state, loader)
        }

        override fun getItem(position: Int): Fragment {
            // TODO: once the other fragments are implemented, return the correct one
            val fmt = AllPhotosFragment()
            fmt.onListener = editModeListener
            return fmt
        }
    }

    companion object {
        const val CLASS_FRAGMENT = 0
        const val PROJECT_FRAGMENT = 1
        const val ALL_FRAGMENT = 2
        const val NUMBER_OF_FRAGMENTS = 3
    }
}
