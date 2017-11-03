package com.example.ubclaunchpad.launchnote

import android.content.Intent
import android.os.Bundle
import android.support.design.internal.BottomNavigationMenu
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import butterknife.BindView
import com.example.ubclaunchpad.launchnote.photoBrowser.PhotoBrowserActivity
import kotlinx.android.synthetic.main.activity_base.*


abstract class BaseActivity : AppCompatActivity() {

    lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentViewId())

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.add_menu_item -> {

                }
                R.id.scan_menu_item -> {

                }
                R.id.browse_menu_item -> {
                    startActivity(Intent(applicationContext, PhotoBrowserActivity::class.java))
                }
            }
            true
        }
    }

    abstract fun getContentViewId(): Int
}