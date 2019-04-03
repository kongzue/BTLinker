package com.kongzue.btutil;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kongzue.btutil.interfaces.BluetoothOpenListener;
import com.kongzue.btutil.interfaces.OnBLEFindServiceListener;
import com.kongzue.btutil.interfaces.OnBLENotificationListener;
import com.kongzue.btutil.interfaces.OnBLEReadListener;
import com.kongzue.btutil.interfaces.OnBLEScanListener;
import com.kongzue.btutil.interfaces.OnBLEWriteListener;
import com.kongzue.btutil.interfaces.OnDeviceLinkStatusChangeListener;
import com.kongzue.btutil.util.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/1/8 17:42
 */
public class BLELinkUtil {
    
    public static final int ERROR_START_BT = -2;            //无法启动蓝牙
    public static final int ERROR_START_BLE = -6;           //设备不支持BLE
    
    public static boolean DEBUGMODE = false;                //是否打印日志
    
    private boolean isScanning = false;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter btAdapter;
    private Context context;
    private OnBLEScanListener onBLEScanListener;                            //查找设备回调
    private OnBLEFindServiceListener onBLEFindServiceListener;              //查询服务回调
    
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    
    private OnDeviceLinkStatusChangeListener onDeviceLinkStatusChangeListener;
    
    private BluetoothGatt bluetoothGatt;                                    //要连接的设备
    private List<BluetoothDevice> deviceList = new ArrayList<>();           //查询到的所有设备
    
    public BLELinkUtil(Context context) {
        this.context = context;
    }
    
    //开启蓝牙设备
    public void openBluetooth(final BluetoothOpenListener openListener) {
        if (!openBt()) {
            //启动蓝牙失败
            loge("启动蓝牙失败");
            if (openListener != null) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        openListener.onResponse(false, ERROR_START_BT, "启动蓝牙失败");
                    }
                });
            }
        } else {
            //启动蓝牙成功
            doCheckBtOpened(openListener);
        }
    }
    
    private BluetoothAdapter bluetooth;                     //获取本地蓝牙适配器，即蓝牙设备
    
    private boolean openBt() {
        bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备
        if (bluetooth == null) {
            //无法打开蓝牙
            loge("无法打开蓝牙");
            return false;
        }
        //启动蓝牙
        if (!bluetooth.isEnabled()) {
            bluetooth.enable();
        }
        return true;
    }
    
    private void doCheckBtOpened(final BluetoothOpenListener openListener) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            loge("设备不支持BLE");
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    openListener.onResponse(false, ERROR_START_BLE, "设备不支持BLE");
                }
            });
            return;
        }
        if (bluetooth.isEnabled()) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    openListener.onResponse(true, 0, null);
                }
            });
        }
    }
    
    //开始查找设备
    public void doScan(OnBLEScanListener listener) {
        if (listener != null) setOnBLEScanListener(listener);
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();
        
        isScanning = true;
        btAdapter.startLeScan(leScanCallback);
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isScanning = false;
                btAdapter.stopLeScan(leScanCallback);
                if (onBLEScanListener != null) {
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onBLEScanListener.onStop();
                            }
                        });
                    } else {
                        onBLEScanListener.onStop();
                    }
                }
            }
        }, 10 * 1000);
    }
    
    //连接指定设备
    public void linkDevice(BluetoothDevice bluetoothDevice, OnBLEFindServiceListener listener) {
        if (listener != null) onBLEFindServiceListener = listener;
        stopScan();
        bluetoothGatt = bluetoothDevice.connectGatt(context, true, bluetoothGattCallback);
    }
    
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (!deviceList.contains(device)) {
                //将设备加入列表数据中
                deviceList.add(device);
            }
            BluetoothDevice linkDevice = onBLEScanListener.onFindDevice(device);
            if (linkDevice != null) {
                linkDevice(linkDevice, null);
                
                isScanning = false;
                btAdapter.stopLeScan(leScanCallback);
            }
            onBLEScanListener.getAllDevice(deviceList);
        }
    };
    
    private String messageCache;
    
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void onMtuChanged(android.bluetooth.BluetoothGatt gatt, int mtu, int status) {
            Log.d("BLE", "onMtuChanged mtu=" + mtu + ",status=" + status);
        }
        
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {     //连接状态改变的回调
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                log("连接成功");
                log("查询该蓝牙设备的服务：" + (bluetoothGatt.discoverServices() ? "开始" : "失败"));
                
                if (onDeviceLinkStatusChangeListener != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onDeviceLinkStatusChangeListener.onLinked();
                        }
                    });
                }
            } else {
                log("断开连接");
                
                if (onDeviceLinkStatusChangeListener != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onDeviceLinkStatusChangeListener.onLinkFailed();
                        }
                    });
                }
            }
        }
        
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {                      //发现服务的回调
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("发现服务成功：");
                final List<BluetoothGattService> supportedGattServices = bluetoothGatt.getServices();
                
                
                if (DEBUGMODE) {
                    for (int i = 0; i < supportedGattServices.size(); i++) {
                        log("服务UUID：" + supportedGattServices.get(i).getUuid());
                        List<BluetoothGattCharacteristic> listGattCharacteristic = supportedGattServices.get(i).getCharacteristics();
                        for (int j = 0; j < listGattCharacteristic.size(); j++) {
                            BluetoothGattCharacteristic gattCharacteristic = listGattCharacteristic.get(j);
                            
                            log("   成员UUID：" + listGattCharacteristic.get(j).getUuid() + "    " +
                                        getPorperties(context, gattCharacteristic)
                            );
                        }
                    }
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    boolean result = bluetoothGatt.requestMtu(512);
                    log("设置长度512：" + result);
                }
                
                
                if (onBLEFindServiceListener != null) {
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onBLEFindServiceListener.onLink(true, supportedGattServices);
                            }
                        });
                    } else {
                        onBLEFindServiceListener.onLink(true, supportedGattServices);
                    }
                }
            } else {
                loge("发现服务失败：status=" + status);
                if (onBLEFindServiceListener != null) {
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onBLEFindServiceListener.onLink(false, null);
                            }
                        });
                    } else {
                        onBLEFindServiceListener.onLink(false, null);
                    }
                }
            }
        }
        
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {      //读操作的回调
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("读取成功：" + characteristic.getStringValue(0));
                String r = "";
                try {
                    r = new String(characteristic.getValue(), "UTF-8");
                } catch (Exception e) {
                    r = characteristic.getStringValue(0);
                }
                final String result = r;
                if (onBLEReadListener != null) {
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onBLEReadListener.onReadMessage(result);
                            }
                        });
                    } else {
                        onBLEReadListener.onReadMessage(result);
                    }
                }
                if (onBLEWriteListenerl != null) {
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (onBLEWriteListenerl.onWrite(true, result)) {
                                    onBLEWriteListenerl = null;
                                }
                            }
                        });
                    } else {
                        if (onBLEWriteListenerl.onWrite(true, result)){
                            onBLEWriteListenerl = null;
                        }
                    }
                }
            }
        }
        
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {     //写操作的回调
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("写入成功");
                
            } else {
                if (onBLEWriteListenerl != null) {
                    if (onBLEWriteListenerl.onWrite(false, null)) {
                        onBLEWriteListenerl = null;
                    }
                }
            }
        }
        
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {               //数据返回的回调
            super.onCharacteristicChanged(gatt, characteristic);
            try {
                String resultCache = "";
                messageCache = messageCache + new String(characteristic.getValue(), "UTF-8").replace("$", "\r\n$");
                if (messageCache.contains("\r\n")) {
                    String[] arrays = messageCache.split("\r\n");
                    resultCache = arrays[0];
                    messageCache = arrays[1];
                }
                //if (messageCache.contains("$") && messageCache.contains("\r\n")) {
                //    int start = messageCache.indexOf("$") + 1;
                //    int end = messageCache.indexOf("\r\n");
                //    resultCache = messageCache.substring(start, end);
                //    messageCache = messageCache
                //            .replace(resultCache, "")
                //            .replace("$", "")
                //            .replace("\r\n", "");
                //}else{
                //    log("cache: "+messageCache);
                //}
                
                if (!isNull(resultCache)) {
                    final String result = resultCache;
                    log("notify:" + result);
                    if (onBLENotificationListener != null) {
                        if (context instanceof AppCompatActivity) {
                            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (onBLEWriteListenerl != null) {
                                        if (onBLEWriteListenerl.onWrite(true, result)) {
                                            onBLEWriteListenerl = null;
                                        }
                                    }
                                    onBLENotificationListener.onGetData(result);
                                }
                            });
                        } else {
                            if (onBLEWriteListenerl != null) {
                                if (onBLEWriteListenerl.onWrite(true, result)) {
                                    onBLEWriteListenerl = null;
                                }
                            }
                            onBLENotificationListener.onGetData(result);
                        }
                    }
                }
            } catch (Exception e) {
            
            }
            
        }
    };
    
    public BLELinkUtil setOnBLEScanListener(OnBLEScanListener onBLEScanListener) {
        this.onBLEScanListener = onBLEScanListener;
        return this;
    }
    
    //停止搜寻
    public void stopScan() {
        isScanning = false;
        if (btAdapter != null) btAdapter.stopLeScan(leScanCallback);
    }
    
    
    private UUID serviceUUID;
    private UUID characteristicUUID;
    
    public void setUUID(UUID s, UUID c) {
        serviceUUID = s;
        characteristicUUID = c;
    }
    
    public void setUUID(String serviceUUIDStr, String characteristicUUIDStr) {
        serviceUUID = UUID.fromString(serviceUUIDStr);
        characteristicUUID = UUID.fromString(characteristicUUIDStr);
    }
    
    private OnBLEReadListener onBLEReadListener;
    private OnBLEWriteListener onBLEWriteListenerl;
    private OnBLENotificationListener onBLENotificationListener;
    
    //读取值
    public void read(String serviceUUID, String characteristicUUID, OnBLEReadListener onBLEReadListener) {
        if (bluetoothGatt == null) {
            loge("未连接到设备，请先查找设备并使用linkDevice(...)方法连接设备");
            return;
        }
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
            this.onBLEReadListener = onBLEReadListener;
            bluetoothGatt.readCharacteristic(characteristic);
        }
    }
    
    public void read(OnBLEReadListener onBLEReadListener) {
        if (bluetoothGatt == null) {
            loge("未连接到设备，请先查找设备并使用linkDevice(...)方法连接设备");
            return;
        }
        if (serviceUUID == null || characteristicUUID == null) {
            loge("未初始化UUID，请先通过setUUID(...)方法初始化UUID");
            return;
        }
        BluetoothGattService service = bluetoothGatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            this.onBLEReadListener = onBLEReadListener;
            bluetoothGatt.readCharacteristic(characteristic);
        }
    }
    
    //写入值
    public boolean write(String serviceUUID, String characteristicUUID, byte[] value, OnBLEWriteListener listener) {
        if (bluetoothGatt == null) {
            loge("未连接到设备，请先查找设备并使用linkDevice(...)方法连接设备");
            return false;
        }
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(serviceUUID));
        
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
            if (listener != null) this.onBLEWriteListenerl = listener;
            characteristic.setValue(value);//new byte[] {0x7e, 0x14, 0x00, 0x00,0x00,(byte) 0xaa}
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            return bluetoothGatt.writeCharacteristic(characteristic);
        } else {
            if (onBLEWriteListenerl != null) onBLEWriteListenerl.onWrite(false, null);
            return false;
        }
    }
    
    public boolean write(String serviceUUID, String characteristicUUID, String value, OnBLEWriteListener listener) {
        return write(serviceUUID, characteristicUUID, value.getBytes(), listener);
    }
    
    public boolean write(byte[] value, OnBLEWriteListener listener) {
        if (bluetoothGatt == null) {
            loge("未连接到设备，请先查找设备并使用linkDevice(...)方法连接设备");
            return false;
        }
        if (serviceUUID == null || characteristicUUID == null) {
            loge("未初始化UUID，请先通过setUUID(...)方法初始化UUID");
            return false;
        }
        BluetoothGattService service = bluetoothGatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            if (listener != null) this.onBLEWriteListenerl = listener;
            characteristic.setValue(value);//new byte[] {0x7e, 0x14, 0x00, 0x00,0x00,(byte) 0xaa}
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            return bluetoothGatt.writeCharacteristic(characteristic);
        } else {
            onBLEWriteListenerl.onWrite(false, null);
            return false;
        }
    }
    
    public boolean write(String value, OnBLEWriteListener listener) {
        log("写入：" + value);
        return write(value.getBytes(), listener);
    }
    
    //写入大数据包
    public void writeBIG(final String serviceUUID, final String characteristicUUID, final byte[] value, final OnBLEWriteListener listener) {
        if (bluetoothGatt == null) {
            loge("未连接到设备，请先查找设备并使用linkDevice(...)方法连接设备");
            return;
        }
        this.onBLEWriteListenerl = listener;
        TaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                int length = value.length;
                while (index < length) {
                    byte[] txBuffer = new byte[20];
                    for (int i = 0; i < 20; i++) {
                        if (index < length) {
                            txBuffer[i] = value[index++];
                        }
                    }
                    boolean result = write(serviceUUID, characteristicUUID, txBuffer, null);
                    if (!result) {
                        if (onBLEWriteListenerl != null) {
                            onBLEWriteListenerl.onWrite(false, null);
                            return;
                        }
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (onBLEWriteListenerl != null) {
                    onBLEWriteListenerl.onWrite(true, null);
                }
            }
        });
    }
    
    public void writeBIG(String serviceUUID, String characteristicUUID, String value, final OnBLEWriteListener listener) {
        writeBIG(serviceUUID, characteristicUUID, value.getBytes(), listener);
    }
    
    public void writeBIG(final byte[] value, final OnBLEWriteListener listener) {
        if (bluetoothGatt == null) {
            loge("未连接到设备，请先查找设备并使用linkDevice(...)方法连接设备");
            return;
        }
        if (serviceUUID == null || characteristicUUID == null) {
            loge("未初始化UUID，请先通过setUUID(...)方法初始化UUID");
            return;
        }
        this.onBLEWriteListenerl = listener;
        TaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                int length = value.length;
                while (index < length) {
                    byte[] txBuffer = new byte[20];
                    for (int i = 0; i < 20; i++) {
                        if (index < length) {
                            txBuffer[i] = value[index++];
                        }
                    }
                    boolean result = write(txBuffer, null);
                    if (!result) {
                        if (onBLEWriteListenerl != null) {
                            onBLEWriteListenerl.onWrite(false, null);
                            return;
                        }
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (onBLEWriteListenerl != null) {
                    onBLEWriteListenerl.onWrite(true, null);
                }
            }
        });
    }
    
    public void writeBIG(String value, OnBLEWriteListener listener) {
        writeBIG(value.getBytes(), listener);
    }
    
    public boolean startGetNotification(String descriptorUUID, OnBLENotificationListener listener) {
        BluetoothGattService service = bluetoothGatt.getService(serviceUUID);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            boolean result = bluetoothGatt.requestMtu(512);
//            log("这是长度512："+result);
//        }
        
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(descriptorUUID));
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            }
            this.onBLENotificationListener = listener;
            return bluetoothGatt.setCharacteristicNotification(characteristic, true);
        } else {
            return false;
        }
    }
    
    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    
    public boolean startGetNotification(String serviceUUID, String characteristicUUID, OnBLENotificationListener listener) {
        this.onBLENotificationListener = listener;
        
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            } else {
                loge("descriptor不存在：");
            }
            return bluetoothGatt.setCharacteristicNotification(characteristic, true);
        } else {
            loge("service不存在：" + serviceUUID.toString());
            return false;
        }
    }
    
    public boolean startGetNotification(OnBLENotificationListener listener) {
        this.onBLENotificationListener = listener;
        
        BluetoothGattService service = bluetoothGatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            }
            return bluetoothGatt.setCharacteristicNotification(characteristic, true);
        } else {
            return false;
        }
    }
    
    public BluetoothGattCharacteristic getCharacteristic(String serviceUUID, String characteristicUUID) {
        if (bluetoothGatt == null) {
            loge("未连接到设备，请先查找设备并使用linkDevice(...)方法连接设备");
            return null;
        }
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(serviceUUID));
        
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
            return characteristic;
        } else {
            return null;
        }
    }
    
    public List<BluetoothDevice> getDeviceList() {
        return deviceList;
    }
    
    public boolean isScanning() {
        return isScanning;
    }
    
    public BLELinkUtil setOnBLEFindServiceListener(OnBLEFindServiceListener onBLEFindServiceListener) {
        this.onBLEFindServiceListener = onBLEFindServiceListener;
        return this;
    }
    
    private void log(String msg) {
        if (DEBUGMODE) Log.i(">>>", "BTLinkUtil:" + msg);
    }
    
    private static void loge(String msg) {
        if (DEBUGMODE) Log.e(">>>", "BTLinkUtil:" + msg);
    }
    
    public String getPorperties(Context context, BluetoothGattCharacteristic item) {
        String proprties;
        String read = null, write = null, notify = null;
        
        if (getGattCharacteristicsPropertices(item.getProperties(), BluetoothGattCharacteristic.PROPERTY_READ)) {
            read = "可读";
        }
        if (getGattCharacteristicsPropertices(item.getProperties(), BluetoothGattCharacteristic.PROPERTY_WRITE)
                | getGattCharacteristicsPropertices(item.getProperties(), BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
            write = "可写";
        }
        if (getGattCharacteristicsPropertices(item.getProperties(), BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            notify = "通知";
        }
        if (getGattCharacteristicsPropertices(item.getProperties(), BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            notify = "指示器";
        }
        
        if (read != null) {
            proprties = read;
            if (write != null) {
                proprties = proprties + "    " + write;
            }
            if (notify != null) {
                proprties = proprties + "    " + notify;
            }
        } else {
            if (write != null) {
                proprties = write;
                
                if (notify != null) {
                    proprties = proprties + "    " + notify;
                }
            } else {
                proprties = notify;
            }
        }
        
        return proprties;
    }
    
    private boolean getGattCharacteristicsPropertices(int characteristics, int characteristicsSearch) {
        if ((characteristics & characteristicsSearch) == characteristicsSearch) {
            return true;
        }
        return false;
    }
    
    //判断特征可读
    public boolean ifCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) > 0);
    }
    
    //判断特征可写
    public boolean ifCharacteristicWritable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0 ||
                (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0);
    }
    
    //判断特征是否具备通知属性
    public boolean ifCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0 ||
                (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0);
    }
    
    public void setOnDeviceLinkedListener(OnDeviceLinkStatusChangeListener onDeviceLinkStatusChangeListener) {
        this.onDeviceLinkStatusChangeListener = onDeviceLinkStatusChangeListener;
    }
    
    public void cancel() {
        if (bluetoothGatt != null) {
            try {
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
            } catch (Exception e) {
            }
        }
    }
    
    public static boolean isNull(String s) {
        if (s == null || s.trim().isEmpty() || s.equals("null") || s.equals("(null)")) {
            return true;
        }
        return false;
    }
}
