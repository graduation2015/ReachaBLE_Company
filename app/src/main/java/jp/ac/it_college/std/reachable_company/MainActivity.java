package jp.ac.it_college.std.reachable_company;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.auth.AWSCredentials;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<AWSCredentials> {

    /** タグ */
    private static final String TAG = "MainActivity";

    /** DrawerLayout 関連フィールド */
    private String[] mPlanetTitles;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /** Toolbar 関連フィールド */
    private Toolbar mToolbar;
    private CharSequence mTitle;

    /** AWSClientManager 関連フィールド */
    private AWSClientManager mClientManager;

    /** ProgressDialogFragment 関連フィールド */
    private ProgressDialogFragment mDialogFragment;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragment(savedInstanceState);
        initProgressDialog();
        initHandler();
        findViews();
        setUpToolbar();
        setUpDrawerLayout();
        initAWSClient();
    }

    private void initHandler() {
        mHandler = new Handler(getMainLooper());
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
     * ProgressDialogFragmentを初期化
     */
    private void initProgressDialog() {
        mDialogFragment = new ProgressDialogFragment().newInstance(
                getString(R.string.dialog_title_credentials), getString(R.string.dialog_message_credentials));
    }

    /** CouponUploadFragmentに切り替える */
    private void initFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container_content, new CouponUploadFragment())
                    .commit();
        }
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
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(
                this, mDrawerList, mDrawerLayout, mToolbar));
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

    /**
     * Credentialsを取得するAsyncTaskLoaderを実行する
     */
    private void initAWSClient() {
        getLoaderManager().restartLoader(0, null, this);
    }

    /**
     * AWSClientManagerのゲッター
     * @return
     */
    public AWSClientManager getClientManager() {
        return mClientManager;
    }

    /**
     * AWSClientManagerのセッター
     * @param credentials
     */
    private void setClientManager(AWSCredentials credentials) {
        mClientManager = new AWSClientManager(credentials);
    }

    /**
     * Handlerのゲッター
     * @return
     */
    private Handler getHandler() {
        return mHandler;
    }

    /** Implemented LoaderManager.LoaderCallbacks */
    /**
     * CognitoAsyncTaskLoaderをインスタンス化して返す
     * @param i
     * @param bundle
     * @return
     */
    @Override
    public Loader<AWSCredentials> onCreateLoader(int i, Bundle bundle) {
        //ProgressDialogを表示
        mDialogFragment.show(getFragmentManager(), TAG);
        return new CognitoAsyncTaskLoader(this);
    }

    /**
     * onCreateLoaderで生成されたローダがロード完了時に呼び出される
     * @param loader
     * @param awsCredentials
     */
    @Override
    public void onLoadFinished(Loader<AWSCredentials> loader, AWSCredentials awsCredentials) {
        //ProgressDialogを非表示
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                mDialogFragment.dismiss();
            }
        });

        //AWSClientManagerをセット
        setClientManager(awsCredentials);
    }

    /**
     * onCreateLoaderで生成されたローダがリセットされ、データが利用不可になった時に呼び出される
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<AWSCredentials> loader) {

    }

}
