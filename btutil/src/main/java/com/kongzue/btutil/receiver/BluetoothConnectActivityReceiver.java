package com.kongzue.btutil.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kongzue.btutil.LinkUtil;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2018/9/2 23:01
 */
public class BluetoothConnectActivityReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
            BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            try {
                //(三星)4.3版本测试手机还是会弹出用户交互页面(闪一下)，如果不注释掉下面这句页面不会取消但可以配对成功。(中兴，魅族4(Flyme 6))5.1版本手机两中情况下都正常
                //ClsUtils.setPairingConfirmation(mBluetoothDevice.getClass(), mBluetoothDevice, true);
                abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                //3.调用setPin方法进行配对...
                boolean ret = LinkUtil.setPin(mBluetoothDevice.getClass(), mBluetoothDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

