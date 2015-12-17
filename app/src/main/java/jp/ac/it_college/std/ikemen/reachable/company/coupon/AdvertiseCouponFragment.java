package jp.ac.it_college.std.ikemen.reachable.company.coupon;


import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ToggleButton;

import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.bluetooth.BluetoothStateChangeListener;
import jp.ac.it_college.std.ikemen.reachable.company.bluetooth.BluetoothStateChangeReceiver;
import jp.ac.it_college.std.ikemen.reachable.company.bluetooth.le.Advertise;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;


public class AdvertiseCouponFragment extends Fragment
        implements View.OnClickListener, BluetoothStateChangeListener {

    /* Bluetooth 関連フィールド */
    private final int REQUEST_ENABLE_BT = 0x01;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothStateChangeReceiver mStateChangeReceiver;
    private IntentFilter mIntentFilter;

    /* Views */
    private ToggleButton mToggleAdvertise;
    private ImageView mCouponPreview;
    private View mContentView;
    private SwitchCompat mAdvertiseSwitch;

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

        //クーポンのプレビューを表示
        setCouponPreview();

        mAdvertise = new Advertise();

        //Bluetoothをセットアップ
        setUpBluetooth();

        getToggleAdvertise().setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //スイッチメニューをインフレート
        inflater.inflate(R.menu.menu_advertise_switch, menu);
        mAdvertiseSwitch = (SwitchCompat) menu.findItem(R.id.menu_advertise_switch).getActionView();
        mAdvertiseSwitch.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Fragmentが破棄されるタイミングでAdvertise停止
        stopAdvertise();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(getStateChangeReceiver(), getIntentFilter());

        //フラグメント表示時にBluetoothが無効の場合ToggleButtonをOffにする
        if (!getBluetoothAdapter().isEnabled()) {
            getToggleAdvertise().setChecked(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(getStateChangeReceiver());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toggle_advertise:
                switchAdvertise(getToggleAdvertise().isChecked());
                break;
            case R.id.switch_advertise:
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
            return;
        }

        //Bluetoothの有効/無効をチェック
        if (getBluetoothAdapter().isEnabled()) {
            if (isAdvertise) {
                startAdvertise();
            } else {
                stopAdvertise();
            }
        } else {
            enableBluetooth();
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
     * クーポンをプレビューにセットする
     */
    private void setCouponPreview() {
        getCouponPreview().setImageBitmap(BitmapFactory.decodeFile(getCouponPath()));
    }

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
     * クーポンのパスを返す
     * @return
     */
    private String getCouponPath() {
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(CouponInfo.FILE_PATH, Context.MODE_PRIVATE);
        return prefs.getString(CouponInfo.FILE_PATH, null);
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
                case Activity.RESULT_OK:
                    /* Bluetooth有効化直後にAdvertiseした場合に
                    メッセージが発信されない現象を回避するため、数秒ディレイをかける*/
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Bluetooth有効化後、数秒待ってAdvertiseを開始する
                            startAdvertise();
                        }
                    }, ADVERTISE_DELAY);
                    break;
                case Activity.RESULT_CANCELED:
                    //ToggleボタンをOFFにする
                    getToggleAdvertise().setChecked(false);
                    break;
            }
        }
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

    public ToggleButton getToggleAdvertise() {
        if (mToggleAdvertise == null) {
            mToggleAdvertise = (ToggleButton) getContentView().findViewById(R.id.toggle_advertise);
        }
        return mToggleAdvertise;
    }

    public ImageView getCouponPreview() {
        if (mCouponPreview == null) {
            mCouponPreview = (ImageView) getContentView().findViewById(R.id.img_coupon_preview);
        }
        return mCouponPreview;
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

    /**
     * BluetoothがOFFになった時に呼ばれる
     */
    @Override
    public void onBluetoothStateOff() {
        //ToggleボタンをOFFにする
        getToggleAdvertise().setChecked(false);
    }
}
