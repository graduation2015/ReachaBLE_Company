package jp.ac.it_college.std.ikemen.reachable.company.coupon;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.ac.it_college.std.ikemen.reachable.company.CouponListAdapter;
import jp.ac.it_college.std.ikemen.reachable.company.EmptySupportRecyclerView;
import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.bluetooth.BluetoothStateChangeListener;
import jp.ac.it_college.std.ikemen.reachable.company.bluetooth.BluetoothStateChangeReceiver;
import jp.ac.it_college.std.ikemen.reachable.company.bluetooth.le.Advertise;


public class AdvertiseCouponFragment extends BaseCouponFragment
        implements View.OnClickListener, BluetoothStateChangeListener {

    /* Bluetooth */
    private final int REQUEST_ENABLE_BT = 0x01;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothStateChangeReceiver mStateChangeReceiver;
    private IntentFilter mIntentFilter;

    /* Views */
    private View mContentView;
    private SwitchCompat mAdvertiseSwitch;
    private EmptySupportRecyclerView mCouponListView;
    private TextView mEmptyView;

    /* BLE */
    private Advertise mAdvertise;
    public static final int ADVERTISE_DELAY = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_advertise_coupon, container, false);

        //初期設定
        initSettings();
        return mContentView;
    }

    /**
     * 初期設定を実行
     */
    private void initSettings() {
        //ツールバーにメニューを表示する
        setHasOptionsMenu(true);

        //Advertiseクラスのインスタンスを生成
        mAdvertise = new Advertise();

        //Bluetoothをセットアップ
        setUpBluetooth();

        //クーポンリストのアダプターをセット
        setCouponListAdapter(new CouponListAdapter(getCouponInfoList()));
        //クーポンリストをセットアップ
        setUpCouponListView(getCouponListView());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //スイッチメニューをインフレート
        inflater.inflate(R.menu.menu_advertise_switch, menu);
        mAdvertiseSwitch = (SwitchCompat) menu.findItem(R.id.menu_advertise_switch).getActionView();
        //クリックリスナーをセット
        mAdvertiseSwitch.setOnClickListener(this);

        //有効化チェック
        checkAdvertise(mAdvertiseSwitch);
    }

    /**
     * クーポンの有無に応じてAdvertiseSwitchの状態を切り替える
     * @param advertiseSwitch ツールバーのAdvertiseSwitch
     */
    private void checkAdvertise(SwitchCompat advertiseSwitch) {
        //クーポンリストが空の場合、AdvertiseSwitchを無効化する
        if (isCouponEmpty()) {
            advertiseSwitch.setEnabled(false);
        }
    }

    /**
     * クーポンリストが空かどうかをチェック
     * @return クーポンリストが空の場合trueを返す
     */
    private boolean isCouponEmpty() {
        return getCouponInfoList().isEmpty();
    }

    @Override
    public void onDestroyView() {
        //Fragmentが破棄されるタイミングでAdvertise停止
        stopAdvertise();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Bluetoothの状態を監視するレシーバを登録する
        getActivity().registerReceiver(getStateChangeReceiver(), getIntentFilter());

        //フラグメント表示時にBluetoothが無効の場合AdvertiseSwitchをOffにする
        if (!getBluetoothAdapter().isEnabled() && getAdvertiseSwitch() != null) {
            getAdvertiseSwitch().setChecked(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //Bluetoothの状態を監視するレシーバを登録解除する
        getActivity().unregisterReceiver(getStateChangeReceiver());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch_advertise: //AdvertiseSwitch押下時の処理
                switchAdvertise(getAdvertiseSwitch().isChecked());
                break;
        }
    }

    /**
     * トグルボタンのON/OFFが切り替わった際に処理を分岐させる
     * @param isAdvertise
     */
    private void switchAdvertise(boolean isAdvertise) {
        if (getBluetoothAdapter() == null) {
            //AdvertiseSwitchをOFFにする
            getAdvertiseSwitch().setChecked(false);
            return;
        } else if (!getBluetoothAdapter().isEnabled()) {
            //AdvertiseSwitchをOFFにする
            getAdvertiseSwitch().setChecked(false);
            enableBluetooth();
            return;
        }

        if (isAdvertise) {
            startAdvertise();
        } else {
            stopAdvertise();
        }
    }

    /**
     * Advertise開始
     */
    private void startAdvertise() {
        if (getBluetoothAdapter() != null) {
            getAdvertise().startAdvertise(getActivity());
        }
    }

    /**
     * Advertise停止
     */
    private void stopAdvertise() {
        if (getBluetoothAdapter() != null) {
            getAdvertise().stopAdvertise();
        }
    }

    /**
     * Bluetooth関連のフィールドを初期化する
     */
    private void setUpBluetooth() {
        //BluetoothAdapterを取得
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //BluetoothStateChangeReceiverを初期化
        mStateChangeReceiver =
                new BluetoothStateChangeReceiver(this);

        //Bluetoothの状態変化を受け取るIntentFilterを生成
        mIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    /**
     * Bluetoothの有効化
     */
    private void enableBluetooth() {
        if (getBluetoothAdapter() == null) {
            return;
        }

        if (!getBluetoothAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            switch (resultCode) {
                case Activity.RESULT_OK: //Bluetooth有効化を許可した場合
                    //数秒ディレイをかけてAdvertiseを開始する
                    delayAdvertise();
                    break;
            }
        }
    }

    /**
     * 数秒ディレイをかけた後にAdvertiseを開始する
     */
    private void delayAdvertise() {
        //AdvertiseSwitchをONにする
        getAdvertiseSwitch().setChecked(true);

        //有効化直後にAdvertiseした場合にメッセージが発信されない現象を回避するため、数秒ディレイをかける
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startAdvertise();
            }
        }, ADVERTISE_DELAY);
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public BluetoothStateChangeReceiver getStateChangeReceiver() {
        return mStateChangeReceiver;
    }

    public IntentFilter getIntentFilter() {
        return mIntentFilter;
    }

    public View getContentView() {
        return mContentView;
    }

    public Advertise getAdvertise() {
        return mAdvertise;
    }

    public SwitchCompat getAdvertiseSwitch() {
        return mAdvertiseSwitch;
    }

    public TextView getEmptyView() {
        if (mEmptyView == null) {
            mEmptyView = (TextView) getContentView().findViewById(R.id.txt_empty_view);
        }
        return mEmptyView;
    }

    public EmptySupportRecyclerView getCouponListView() {
        if (mCouponListView == null) {
            mCouponListView = (EmptySupportRecyclerView) getContentView().findViewById(R.id.coupon_list);
            //リストが空の際に表示するViewをセット
            mCouponListView.setEmptyView(getEmptyView());
        }
        return mCouponListView;
    }


    /**
     * BluetoothがOFFになった時に呼ばれる
     */
    @Override
    public void onBluetoothStateOff() {
        //AdvertiseSwitchをOFFにする
        getAdvertiseSwitch().setChecked(false);
    }
}
