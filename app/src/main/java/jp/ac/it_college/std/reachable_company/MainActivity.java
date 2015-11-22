package jp.ac.it_college.std.reachable_company;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.auth.AWSCredentials;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<AWSCredentials> {

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

    /* AwsClientManager 関連フィールド */
    private AwsClientManager mClientManager;

    /* ProgressDialogFragment 関連フィールド */
    private ProgressDialogFragment mDialogFragment;
    private Handler mHandler;

    /* MainActivity 関連フィールド */
    private Bundle mSavedInstanceState;
    private static final int COGNITO_ASYNC_TASK_LOADER_ID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初期設定
        initSettings();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.mSavedInstanceState = outState;
    }

    /**
     * 初期設定を実行
     */
    private void initSettings() {
        mDialogFragment = new ProgressDialogFragment().newInstance(
                getString(R.string.dialog_title_credentials), getString(R.string.dialog_message_credentials));
        mHandler = new Handler(getMainLooper());

        //ToolBarを設定
        setUpToolbar();
        //DrawerListenerにDrawerToggleをセット
        getDrawerLayout().setDrawerListener(getDrawerToggle());
        //サイドメニューを設定
        setUpDrawerList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getClientManager() == null) {
            // AWSClientManagerを初期化
            initAWSClient();
        }
    }


    /**
     * Fragmentを切り替える
     * @param fragment
     */
    private void changeFragment(Fragment fragment) {
        if (getSavedInstanceState() == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container_content, fragment)
                    .commit();
        }
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    /**
     * Credentialsを取得するAsyncTaskLoaderを実行する
     */
    private void initAWSClient() {
        getLoaderManager().initLoader(COGNITO_ASYNC_TASK_LOADER_ID, null, this);
    }

    /**
     * AWSClientManagerを返す
     * @return
     */
    public AwsClientManager getClientManager() {
        return mClientManager;
    }

    /**
     * AWSClientManagerをセット
     * @param credentials
     */
    private void setClientManager(AWSCredentials credentials) {
        mClientManager = new AwsClientManager(credentials);
    }

    /* Implemented LoaderManager.LoaderCallbacks */

    /**
     * CognitoAsyncTaskLoaderをインスタンス化して返す
     * @param i
     * @param bundle
     * @return
     */
    @Override
    public Loader<AWSCredentials> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader");
        //ProgressDialogを表示
        mDialogFragment.show(getFragmentManager(), TAG);
        return new CognitoAsyncTaskLoader(this);
    }
    /**
     * onCreateLoaderで生成されたローダのロード完了時に呼び出される
     * @param loader
     * @param awsCredentials
     */
    @Override
    public void onLoadFinished(Loader<AWSCredentials> loader, AWSCredentials awsCredentials) {
        Log.d(TAG, "onLoadFinished");

        //AWSClientManagerをセット
        setClientManager(awsCredentials);

        getHandler().post(new Runnable() {
            @Override
            public void run() {
                //ProgressDialogを非表示
                mDialogFragment.dismiss();

                //CouponUploadFragmentに切り替える
                changeFragment(new CouponUploadFragment());
            }
        });

        //Loaderを破棄
        getLoaderManager().destroyLoader(COGNITO_ASYNC_TASK_LOADER_ID);
    }

    /**
     * onCreateLoaderで生成されたローダがリセットされ、データが利用不可になった時に呼び出される
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<AWSCredentials> loader) {
        Log.d(TAG, "onLoaderReset");
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

    /**
     * Handlerを返す
     * @return
     */
    private Handler getHandler() {
        return mHandler;
    }

    /**
     * SavedInstanceStateを返す
     * @return
     */
    public Bundle getSavedInstanceState() {
        return mSavedInstanceState;
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
