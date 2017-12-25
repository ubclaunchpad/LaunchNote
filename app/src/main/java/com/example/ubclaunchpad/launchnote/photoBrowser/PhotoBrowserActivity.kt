package com.example.ubclaunchpad.launchnote.photoBrowser

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import butterknife.ButterKnife
import com.example.ubclaunchpad.launchnote.BaseActivity
import com.example.ubclaunchpad.launchnote.R
import kotlinx.android.synthetic.main.activity_photo_browser.*

/**
 * Activity for browsing photos in different folders
 */
class PhotoBrowserActivity : BaseActivity() {

    lateinit var customPagerAdapter: CustomPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)

        // set up ViewPager
        // this lets us swipe between the three different ways of browsing photos:
        // by LaunchNoteClass, by Project, or by All photos
        // each of them is represented by a fragment
        customPagerAdapter = CustomPagerAdapter(supportFragmentManager)

        view_pager.adapter = customPagerAdapter
        view_pager.currentItem = ALL_FRAGMENT
        // add listener to view_pager that will update buttons when user scrolls to new page
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
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.getItem(BROWSE_MENU_ITEM).isChecked = true
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_photo_browser
    }

    private fun updateButtons(fragmentId: Int) {
        class_button.setTextColor(resources.getColor(R.color.material_grey_850))
        project_button.setTextColor(resources.getColor(R.color.material_grey_850))
        all_button.setTextColor(resources.getColor(R.color.material_grey_850))

        when (fragmentId) {
            CLASS_FRAGMENT -> class_button.setTextColor(resources.getColor(R.color.colorAccent))
            PROJECT_FRAGMENT -> project_button.setTextColor(resources.getColor(R.color.colorAccent))
            ALL_FRAGMENT -> all_button.setTextColor(resources.getColor(R.color.colorAccent))
        }
    }

    /**
     * FragmentPagerAdapter class
     */
    class CustomPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        val NUM_ITEMS = 3

        override fun getCount(): Int {
            return NUM_ITEMS
        }

        override fun getItem(position: Int): Fragment {
            // TODO: once the other fragments are implemented, return the correct one
            return AllPhotosFragment()
        }
    }

    companion object {
        const val CLASS_FRAGMENT = 0
        const val PROJECT_FRAGMENT = 1
        const val ALL_FRAGMENT = 2
    }
}
