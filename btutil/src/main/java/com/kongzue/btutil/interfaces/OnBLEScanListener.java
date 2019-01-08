package com.kongzue.btutil.interfaces;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/1/8 21:38
 */
public interface OnBLEScanListener {
    void onScan(List<BluetoothDevice> deviceList);
}
