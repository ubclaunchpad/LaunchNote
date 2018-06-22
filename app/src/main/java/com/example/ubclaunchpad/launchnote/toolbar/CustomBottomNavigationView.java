package com.example.ubclaunchpad.launchnote.toolbar;

import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

/**
 * Created by vpineda on 6/6/2018.
 */

public class CustomBottomNavigationView extends BottomNavigationView {

    public CustomBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        centerMenuIcon();
    }

    private void centerMenuIcon() {
        BottomNavigationMenuView menuView = getBottomMenuView();

        if (menuView != null) {
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView menuItemView = (BottomNavigationItemView) menuView.getChildAt(i);

                AppCompatImageView icon = (AppCompatImageView) menuItemView.getChildAt(0);

                FrameLayout.LayoutParams params = (LayoutParams) icon.getLayoutParams();
                params.gravity = Gravity.CENTER;
                menuItemView.setShiftingMode(true);
            }
        }
    }

    private BottomNavigationMenuView getBottomMenuView() {
        Object menuView = null;
        try {
            Field field = BottomNavigationView.class.getDeclaredField("mMenuView");
            field.setAccessible(true);
            menuView = field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return (BottomNavigationMenuView) menuView;
    }
}
