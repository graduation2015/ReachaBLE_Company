package jp.ac.it_college.std.reachable_company.bluetooth.le;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.os.Build;

import java.util.Arrays;

import jp.ac.it_college.std.reachable_company.info.CompanyInfo;

public class BLEServer extends BluetoothGattServerCallback {

    private BluetoothGattServer bluetoothGattServer;

    //セントラル（クライアント）からReadRequestが来ると呼ばれる
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId,
                                            int offset, BluetoothGattCharacteristic characteristic) {

        //クライアントにキー名を送信
        setCharacteristicValue(characteristic, offset, CompanyInfo.COMPANY_ID.getBytes());
        bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                characteristic.getValue());
    }

    //セントラル（クライアント）からWriteRequestが来ると呼ばれる
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded,
                                             int offset, byte[] value) {
        //セントラルにnullを返信する
        bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
    }

    /**
     * 文字列を分割する
     * @param original
     * @param offset
     * @return
     */
    private byte[] extractAry(byte[] original, int offset) {
        return Arrays.copyOfRange(original, offset, original.length);
    }

    /**
     * Characteristicに値をセットする
     * @param characteristic
     * @param offset
     * @param values
     */
    private void setCharacteristicValue(BluetoothGattCharacteristic characteristic,
                                        int offset, byte[] values) {
        characteristic.setValue(extractAry(values, offset));
    }

    /**
     * BluetoothGattServerのセッター
     * @param bluetoothGattServer
     */
    public void setBluetoothGattServer(BluetoothGattServer bluetoothGattServer) {
        this.bluetoothGattServer = bluetoothGattServer;
    }
}
