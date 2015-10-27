package jp.ac.it_college.std.reachable_company;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    //DrawerLayout関連
    private String[] mPlanetTitles;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    //Toolbar関連
    private Toolbar mToolbar;
    private CharSequence mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setUpToolbar();
        setUpDrawerLayout();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * DrawerLayoutをセットアップする
     */
    private void setUpDrawerLayout() {
        //ページのタイトルを取得
        mTitle = getTitle();

        //ActionBarDrawerToggleを生成
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar
                ,R.string.drawer_open, R.string.drawer_close) {
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
        };
        //DrawerListenerにDrawerToggleをセット
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //サイドメニューに表示するメニューをリソースから取得
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);

        //サイドメニューリスト用アダプターをセット
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, mPlanetTitles);
        mDrawerList.setAdapter(listAdapter);

        //サイドメニューのonClickListenerをセット
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    /**
     * Toolbarをアクションバーとしてセットする
     */
    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * レイアウトからViewを取得する
     */
    private void findViews() {
        mDrawerList = (ListView) findViewById(R.id.side_menu_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }
}
