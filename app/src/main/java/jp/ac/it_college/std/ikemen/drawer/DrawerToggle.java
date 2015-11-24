package jp.ac.it_college.std.ikemen.drawer;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DrawerToggle extends ActionBarDrawerToggle {

    public DrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar,
                        int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        //ドロワーを開いた時に呼ばれる
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        //ドロワーを閉じた時に呼ばれる
    }
}
