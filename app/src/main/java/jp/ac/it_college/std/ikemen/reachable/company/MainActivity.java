package jp.ac.it_college.std.ikemen.reachable.company;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import jp.ac.it_college.std.ikemen.reachable.company.coupon.CouponPreviewFragment;
import jp.ac.it_college.std.ikemen.reachable.company.drawer.DrawerToggle;

public class MainActivity extends AppCompatActivity {

    /* Constants */
    private static final String TAG = "tag_MainActivity";

    /* DrawerLayout */
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    /* Toolbar */
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container_content, new CouponPreviewFragment())
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

    public NavigationView getNavigationView() {
        if (mNavigationView == null) {
            mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        }

        return mNavigationView;
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
