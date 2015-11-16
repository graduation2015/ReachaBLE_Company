package jp.ac.it_college.std.reachable_company;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class AdvertiseCouponFragment extends Fragment
        implements View.OnClickListener {

    /* Bluetooth 関連フィールド */
    private final int REQUEST_ENABLE_BT = 0x01;
    private BluetoothAdapter mBluetoothAdapter;

    /* Views */
    private ToggleButton mToggleAdvertise;
    private ImageView mCouponPreview;

    /* BLE */
    private Advertise advertise;

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

        return contentView;
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
        Toast.makeText(getActivity(), "ON", Toast.LENGTH_SHORT).show();
        advertise.startAdvertise(getActivity());
    }

    /**
     * Advertise停止
     */
    private void stopAdvertise() {
        Toast.makeText(getActivity(), "OFF", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();
                    startAdvertise();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getActivity(), "CANCEL", Toast.LENGTH_SHORT).show();

                    //ToggleボタンをOFFにする
                    mToggleAdvertise.setChecked(false);
                    break;
            }
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }
}
