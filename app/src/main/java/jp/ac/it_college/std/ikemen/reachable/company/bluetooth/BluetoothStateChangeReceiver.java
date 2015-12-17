package jp.ac.it_college.std.ikemen.reachable.company.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothStateChangeReceiver extends BroadcastReceiver {
    private final BluetoothStateChangeListener listener;

    public BluetoothStateChangeReceiver(BluetoothStateChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE);

        switch (state) {
            case BluetoothAdapter.STATE_ON:
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                break;
            case BluetoothAdapter.STATE_OFF:
                this.listener.onBluetoothStateOff();
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                break;
        }
    }
}
