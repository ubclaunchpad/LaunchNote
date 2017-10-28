package com.example.ubclaunchpad.launchnote

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.ubclaunchpad.launchnote.addPhoto.GalleryActivity
import com.example.ubclaunchpad.launchnote.galleries.AllPhotosFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : FragmentActivity() {
    lateinit var customPagerAdapter: CustomPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        // set up ViewPager
        customPagerAdapter = CustomPagerAdapter(supportFragmentManager)

        view_pager.adapter = customPagerAdapter
        view_pager.currentItem = ALL_FRAGMENT

        // Watch for button clicks
        class_button.setOnClickListener { view_pager.currentItem = CLASS_FRAGMENT }
        project_button.setOnClickListener { view_pager.currentItem = PROJECT_FRAGMENT }
        all_button.setOnClickListener { view_pager.currentItem = ALL_FRAGMENT }
    }

    @OnClick(R.id.buttonUploadFromGallery)
    fun launchGalleryActivity(view: View) =
            startActivity(Intent(this, GalleryActivity::class.java))

    /**
     * FragmentPagerAdapter class
     */
    class CustomPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        val NUM_ITEMS = 3

        override fun getCount(): Int {
            return NUM_ITEMS
        }

        override fun getItem(position: Int): Fragment {
            return AllPhotosFragment()
        }
    }

    companion object {
        const val CLASS_FRAGMENT = 0
        const val PROJECT_FRAGMENT = 1
        const val ALL_FRAGMENT = 2
    }
}
