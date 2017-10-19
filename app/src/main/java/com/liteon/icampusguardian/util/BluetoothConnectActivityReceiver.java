package com.liteon.icampusguardian.util;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Method;

public class BluetoothConnectActivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
            BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            try {
                //ClsUtils.setPairingConfirmation(mBluetoothDevice.getClass(), mBluetoothDevice, true);
                abortBroadcast();
                //3.調用setPin方法進行配對...
                boolean ret = ClsUtils.setPin(mBluetoothDevice.getClass(), mBluetoothDevice, "你需要設置的PIN碼");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
