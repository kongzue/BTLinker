package com.kongzue.btutil;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.kongzue.btutil.interfaces.OnBLEScanListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/1/8 17:42
 */
public class BLELinkUtil {
    
    public static boolean DEBUGMODE = false;                //是否打印日志
    
    private boolean isScanning = false;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter btAdapter;
    private Context context;
    private OnBLEScanListener onBLEScanListener;
    
    //开始
    public void start(Context c) {
        this.context = c;
        
        if (!openBt(c)) {
            //启动蓝牙失败
            
        } else {
            //启动蓝牙成功
            doCheckBtOpened(c);
        }
    }
    
    private Timer linkTimer;
    private BluetoothAdapter bluetooth;                     //获取本地蓝牙适配器，即蓝牙设备
    
    private void doCheckBtOpened(Context c) {
        if (!c.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //不支持BLE
            
            return;
        }
        this.context = c;
        if (linkTimer != null) linkTimer.cancel();
        linkTimer = new Timer();
        linkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (bluetooth.isEnabled() != false) {
                    doScan(context);
                    linkTimer.cancel();
                }
            }
        }, 1000, 1000);
    }
    
    private BluetoothGatt bluetoothGatt;
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    
    private void doScan(Context c) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();
        
        isScanning = true;
        btAdapter.startLeScan(leScanCallback);
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isScanning = false;
                btAdapter.stopLeScan(leScanCallback);
            }
        }, 10 * 1000);
    }
    
    //连接指定设备
    public void linkDevice(BluetoothDevice bluetoothDevice) {
        stopScan();
        bluetoothGatt = bluetoothDevice.connectGatt(context, true, bluetoothGattCallback);
    }
    
    private boolean openBt(Context context) {
        bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备
        if (bluetooth == null) {
            //无法打开蓝牙
            loge("无法打开蓝牙");
            
            return false;
        }
        //启动蓝牙
        if (bluetooth.isEnabled() == false) {
            bluetooth.enable();
        }
        return true;
    }
    
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (!deviceList.contains(device)) {
                //将设备加入列表数据中
                deviceList.add(device);
            }
            onBLEScanListener.onScan(deviceList);
        }
    };
    
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {     //连接状态改变的回调
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                log("连接成功");
            } else {
                log("断开连接");
            }
        }
        
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {                      //发现服务的回调
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("发现服务成功");
            } else {
                loge("发现服务失败：status=" + status);
            }
        }
        
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {      //读操作的回调
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("读取成功");
            }
        }
        
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {     //写操作的回调
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("写入成功");
            }
        }
        
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {               //数据返回的回调
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };
    
    private void log(String msg) {
        if (DEBUGMODE) Log.i(">>>", "BTLinkUtil:" + msg);
    }
    
    private static void loge(String msg) {
        if (DEBUGMODE) Log.e(">>>", "BTLinkUtil:" + msg);
    }
    
    public BLELinkUtil setOnBLEScanListener(OnBLEScanListener onBLEScanListener) {
        this.onBLEScanListener = onBLEScanListener;
        return this;
    }
    
    //停止搜寻
    public void stopScan() {
        isScanning = false;
        if (btAdapter != null) btAdapter.stopLeScan(leScanCallback);
    }
}
