package jp.ac.it_college.std.ikemen.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

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
                Toast.makeText(context, "Bluetooth On", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                Toast.makeText(context, "Bluetooth Turning On", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothAdapter.STATE_OFF:
                Toast.makeText(context, "Bluetooth Off", Toast.LENGTH_SHORT).show();
                this.listener.onBluetoothStateOff();
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                Toast.makeText(context, "Bluetooth Turning Off", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
