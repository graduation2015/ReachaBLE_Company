package jp.ac.it_college.std.ikemen.reachable.company;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import jp.ac.it_college.std.ikemen.reachable.company.coupon.CouponUploadFragment;
import jp.ac.it_college.std.ikemen.reachable.company.drawer.DrawerItemClickListener;
import jp.ac.it_college.std.ikemen.reachable.company.drawer.DrawerToggle;

public class MainActivity extends AppCompatActivity {

    /** タグ */
    private static final String TAG = "tag_MainActivity";

    /* DrawerLayout 関連フィールド */
    private String[] mSideMenuTitles;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<String> mSideMenuArrayAdapter;

    /* Toolbar 関連フィールド */
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container_content, new CouponUploadFragment())
                    .commit();
        }

        //初期設定
        initSettings();
    }

    /**
     * 初期設定を実行
     */
    private void initSettings() {
        //ToolBarを設定
        setUpToolbar();
        //DrawerListenerにDrawerToggleをセット
        getDrawerLayout().setDrawerListener(getDrawerToggle());
        //サイドメニューを設定
        setUpDrawerList();
    }

    /**
     * サイドメニューを設定する
     */
    private void setUpDrawerList() {
        //サイドメニュー用ArrayAdapterをセット
        getDrawerList().setAdapter(getSideMenuArrayAdapter());
        //サイドメニューのonItemClickListenerをセット
        getDrawerList().setOnItemClickListener(new DrawerItemClickListener(
                this, getDrawerList(), getDrawerLayout(), getToolbar(), getSideMenuTitles()));
    }

    /**
     * Toolbarをアクションバーとしてセットする
     */
    private void setUpToolbar() {
        setSupportActionBar(getToolbar());
    }

    public DrawerLayout getDrawerLayout() {
        if (mDrawerLayout == null) {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        }

        return mDrawerLayout;
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        if (mDrawerToggle == null) {
            mDrawerToggle = new DrawerToggle(
                    this, getDrawerLayout(), getToolbar(), R.string.drawer_open, R.string.drawer_close);
        }

        return mDrawerToggle;
    }

    public Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }

        return mToolbar;
    }

    public String[] getSideMenuTitles() {
        if (mSideMenuTitles == null) {
            mSideMenuTitles = getResources().getStringArray(R.array.side_menu_titles);
        }
        return mSideMenuTitles;
    }

    public ListView getDrawerList() {
        if (mDrawerList == null) {
            mDrawerList = (ListView) findViewById(R.id.side_menu_list);
        }
        return mDrawerList;
    }

    public ArrayAdapter<String> getSideMenuArrayAdapter() {
        if (mSideMenuArrayAdapter == null) {
            mSideMenuArrayAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_list_item_1, getSideMenuTitles());
        }
        return mSideMenuArrayAdapter;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDrawerToggle().onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
