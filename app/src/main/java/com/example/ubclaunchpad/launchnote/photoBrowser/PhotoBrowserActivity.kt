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
        // by Folder, or by All photos
        // each of them is represented by a fragment
        customPagerAdapter = CustomPagerAdapter(supportFragmentManager, this)
        toolbarFragment = supportFragmentManager.findFragmentById(R.id.photoBrowserToolbarFragment) as PhotoNavigatonToolbarFragment

        view_pager.adapter = customPagerAdapter
        // todo vpineda should we be reading the current item from the bundle rather than hardcoding it?
        updateButtons(BROWSER.ALL)
        // add listener to view_pager that will update elements when user scrolls to new page
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                // do nothing
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // do nothing
            }

            override fun onPageSelected(position: Int) {
                // when new page is selected, update button
                updateButtons(BROWSER.fromOrdinal(position), updateViewPager = false)
            }

        })

        // Watch for button clicks. When a button is clicked, go to the correct fragment
        folder_button.setOnClickListener { updateButtons(BROWSER.FOLDER) }
        all_button.setOnClickListener { updateButtons(BROWSER.ALL) }

        // Restore state of fragments
        BROWSER.values()
                .map {
                    // todo vpineda generalize to the diff types of photo fragments
                    supportFragmentManager.findFragmentByTag("android:switcher:${R.id.view_pager}:${it.ordinal}") as AllPhotosFragment?
                }
                .forEach { f ->
                    f?.let {
                        it.onListener = this
                    }
                }
    }

    override fun onEditPhotoMode(isActiveEdit: Boolean, imagesSelected: Set<PicNote>) {
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
        when(butonInfo) {
            R.id.edit_toolbar_delete_btn -> {
                customPagerAdapter.currentFragment!!.removeSelection()
            }
            R.id.edit_toolbar_edit_desc_btn -> {
                customPagerAdapter.currentFragment!!.openEditView()
            }
            else -> {
                customPagerAdapter.currentFragment!!.cancelSelection()
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

    private fun updateButtons(fragmentId: BROWSER, updateViewPager: Boolean = true) {
        if (updateViewPager) view_pager.currentItem = fragmentId.ordinal;
        folder_button.setTextColor(resources.getColor(R.color.darkGreyText))
        all_button.setTextColor(resources.getColor(R.color.darkGreyText))

        when (fragmentId) {
            BROWSER.FOLDER -> folder_button.setTextColor(resources.getColor(R.color.colorAccent))
            BROWSER.ALL -> all_button.setTextColor(resources.getColor(R.color.colorAccent))
        }
    }

    /**
     * FragmentPagerAdapter class
     */
    class CustomPagerAdapter(fragmentManager: FragmentManager, val editModeListener: AllPhotosFragment.OnEditPhotoMode) : FragmentPagerAdapter(fragmentManager) {
        val NUM_ITEMS = 3
        var currentFragment: AllPhotosFragment? = null

        override fun getCount(): Int {
            return NUM_ITEMS
        }

        override fun setPrimaryItem(container: ViewGroup?, position: Int, `object`: Any?) {
            super.setPrimaryItem(container, position, `object`)

            if (currentFragment != `object`) {
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
        enum class BROWSER {
            FOLDER, ALL;

            companion object {
                fun fromOrdinal(ordinal: Int): BROWSER = BROWSER.values()[ordinal]
            }
        }
    }
}
