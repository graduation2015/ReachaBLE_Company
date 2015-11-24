package jp.ac.it_college.std.reachable_company;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import jp.ac.it_college.std.reachable_company.bluetooth.BluetoothStateChangeListener;
import jp.ac.it_college.std.reachable_company.bluetooth.BluetoothStateChangeReceiver;
import jp.ac.it_college.std.reachable_company.bluetooth.le.Advertise;


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

    /* BLE */
    private Advertise advertise;
    public static final int ADVERTISE_DELAY = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_advertise_coupon, container, false);

        //ビューの取得
        findViews(contentView);

        //クーポンのプレビューを表示
        setCouponPreview();

        //Advertiseのセットアップ
        setUpAdvertise();

        //BluetoothAdapterを取得
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //BluetoothStateChangeReceiverを初期化
        mStateChangeReceiver =
                new BluetoothStateChangeReceiver(this);

        //Bluetoothの状態変化を受け取るIntentFilterを生成
        mIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

        return contentView;
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
            mToggleAdvertise.setChecked(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(getStateChangeReceiver());
    }

    private void setUpAdvertise() {
        advertise = new Advertise();
    }

    private void findViews(View contentView) {
        mToggleAdvertise = (ToggleButton) contentView.findViewById(R.id.toggle_advertise);
        mToggleAdvertise.setOnClickListener(this);

        mCouponPreview = (ImageView) contentView.findViewById(R.id.img_coupon_preview);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toggle_advertise:
                switchAdvertise(mToggleAdvertise.isChecked());
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
            setUpBluetooth();
        }
    }

    /**
     * Advertise開始
     */
    private void startAdvertise() {
        Toast.makeText(getActivity(), "Advertise On", Toast.LENGTH_SHORT).show();
        advertise.startAdvertise(getActivity());
    }

    /**
     * Advertise停止
     */
    private void stopAdvertise() {
        Toast.makeText(getActivity(), "Advertise Off", Toast.LENGTH_SHORT).show();
        advertise.stopAdvertise();
    }

    /**
     * クーポンをプレビューにセットする
     */
    private void setCouponPreview() {
        mCouponPreview.setImageBitmap(BitmapFactory.decodeFile(getCouponPath()));
    }

    /**
     * クーポンのパスを返す
     * @return
     */
    private String getCouponPath() {
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(Constants.COUPON_FILE_PATH, Context.MODE_PRIVATE);
        return prefs.getString(Constants.COUPON_FILE_PATH, null);
    }

    /**
     * Bluetoothの有効化
     */
    private void setUpBluetooth() {
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
                    mToggleAdvertise.setChecked(false);
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

    /**
     * BluetoothがOFFになった時に呼ばれる
     */
    @Override
    public void onBluetoothStateOff() {
        //ToggleボタンをOFFにする
        mToggleAdvertise.setChecked(false);
    }
}
