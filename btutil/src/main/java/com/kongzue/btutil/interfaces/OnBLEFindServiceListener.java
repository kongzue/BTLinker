package com.kongzue.btutil.interfaces;

import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/1/9 20:38
 */
public interface OnBLEFindServiceListener {
    
    void onLink(boolean isSuccess, List<BluetoothGattService> services);
}
