package jp.ac.it_college.std.reachable_company;

import android.app.Activity;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.EnumMap;

public class DrawerItemClickListener implements AdapterView.OnItemClickListener {

    private Activity mActivity;
    private ListView mDrawerList;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;

    public DrawerItemClickListener(Activity activity, ListView drawerList, String[] planetTitles, DrawerLayout drawerLayout) {
        this.mActivity = activity;
        this.mDrawerList = drawerList;
        this.mPlanetTitles = planetTitles;
        this.mDrawerLayout = drawerLayout;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private void selectItem(PageItems items) {
        switch (items) {

        }
    }
}
