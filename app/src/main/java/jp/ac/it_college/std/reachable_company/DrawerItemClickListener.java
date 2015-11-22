package jp.ac.it_college.std.reachable_company;

import android.app.Activity;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * DrawerLayoutのメニューアイテムがクリックされた時のイベントリスナークラス
 */
public class DrawerItemClickListener implements AdapterView.OnItemClickListener {

    private Activity mActivity;
    private ListView mDrawerList;
    private String[] mSideMenuTitles;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    /**
     * コンストラクタ
     * @param activity
     * @param drawerList
     * @param drawerLayout
     * @param toolbar
     */
    public DrawerItemClickListener(Activity activity, ListView drawerList,
                                   DrawerLayout drawerLayout, Toolbar toolbar, String[] sideMenuTitles) {
        this.mActivity = activity;
        this.mDrawerList = drawerList;
        this.mDrawerLayout = drawerLayout;
        this.mToolbar = toolbar;
        this.mSideMenuTitles = sideMenuTitles;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        selectItem(PageItems.values()[i]);
    }

    /**
     * PageItems列挙子に応じたページに切り替える
     * @param items
     */
    private void selectItem(PageItems items) {
        Fragment fragment = PageFactory.getPageItem(items);
        pageChange(fragment);

        mDrawerList.setItemChecked(items.ordinal(), true);
        mToolbar.setTitle(mSideMenuTitles[items.ordinal()]);
        mDrawerLayout.closeDrawers();
    }

    /**
     * フラグメントを切り替える
     * @param fragment
     */
    private void pageChange(Fragment fragment) {
        mActivity.getFragmentManager().beginTransaction()
                .replace(R.id.container_content, fragment)
                .commit();
    }
}
