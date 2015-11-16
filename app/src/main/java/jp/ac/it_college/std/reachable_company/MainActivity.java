package jp.ac.it_college.std.reachable_company;

import android.app.Fragment;
import android.app.LoaderManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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

    /* Bluetooth 関連フィールド */
    private final int REQUEST_ENABLE_BT = 0x01;

    /* DrawerLayout 関連フィールド */
    private String[] mPlanetTitles;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /* Toolbar 関連フィールド */
    private Toolbar mToolbar;
    private CharSequence mTitle;

    /* AWSClientManager 関連フィールド */
    private AWSClientManager mClientManager;

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

        setUpToolbar();
        setUpDrawerLayout();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getClientManager() == null) {
            // AWSClientManagerを初期化
            initAWSClient();
        }

        //Bluetoothを有効化
        setUpBluetooth();

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
     * Bluetoothの有効化
     */
    private void setUpBluetooth() {
        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();

        if (bt == null) {
            return;
        }

        if (!bt.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    /**
     * DrawerLayoutをセットアップする
     */
    private void setUpDrawerLayout() {
        //レイアウトを取得
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //リストを取得
        mDrawerList = (ListView) findViewById(R.id.side_menu_list);

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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
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
    public AWSClientManager getClientManager() {
        return mClientManager;
    }

    /**
     * AWSClientManagerを返す
     * @param credentials
     */
    private void setClientManager(AWSCredentials credentials) {
        mClientManager = new AWSClientManager(credentials);
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

}
