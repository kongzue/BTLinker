package com.kongzue.btutil.interfaces;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.ListView;

import java.util.List;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/1/8 21:38
 */
public interface OnBLEScanListener {
    BluetoothDevice onFindDevice(BluetoothDevice device);
    
    void getAllDevice(List<BluetoothDevice> devices);
    
    void onStop();
}
