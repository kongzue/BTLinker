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
import android.util.Log;

import com.kongzue.btutil.interfaces.OnBLEFindServiceListener;
import com.kongzue.btutil.interfaces.OnBLENotificationListener;
import com.kongzue.btutil.interfaces.OnBLEReadListener;
import com.kongzue.btutil.interfaces.OnBLEScanListener;
import com.kongzue.btutil.interfaces.OnBLEWriteListener;
import com.kongzue.btutil.util.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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
    private OnBLEScanListener onBLEScanListener;                            //查找设备回调
    private OnBLEFindServiceListener onBLEFindServiceListener;              //查询服务回调
    
    private BluetoothGatt bluetoothGatt;                                    //要连接的设备
    private List<BluetoothDevice> deviceList = new ArrayList<>();           //查询到的所有设备
    
    //开始搜寻设备
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
            loge("不支持BLE");
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
    
    private void doScan(Context c) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();
        
        isScanning = true;
        btAdapter.startLeScan(leScanCallback);
        
        Looper.prepare();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isScanning = false;
                btAdapter.stopLeScan(leScanCallback);
            }
        }, 10 * 1000);
    }
    
    //连接指定设备
    public void linkDevice(BluetoothDevice bluetoothDevice, OnBLEFindServiceListener listener) {
        if (listener != null) onBLEFindServiceListener = listener;
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
            BluetoothDevice linkDevice = onBLEScanListener.onFindDevice(device);
            if (linkDevice != null) {
                linkDevice(linkDevice, null);
                
                isScanning = false;
                btAdapter.stopLeScan(leScanCallback);
            }
            onBLEScanListener.getAllDevice(deviceList);
        }
    };
    
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
            } else {
                log("断开连接");
            }
        }
        
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {                      //发现服务的回调
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("发现服务成功：");
                List<BluetoothGattService> supportedGattServices = bluetoothGatt.getServices();
                if (onBLEFindServiceListener != null)
                    onBLEFindServiceListener.onLink(true, supportedGattServices);
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
                    log("这是长度512：" + result);
                }
            } else {
                loge("发现服务失败：status=" + status);
                if (onBLEFindServiceListener != null) onBLEFindServiceListener.onLink(false, null);
            }
        }
        
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {      //读操作的回调
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("读取成功：" + characteristic.getStringValue(0));
                if (onBLEReadListener != null) {
                    String result = "";
                    try {
                        result = new String(characteristic.getValue(), "UTF-8");
                    } catch (Exception e) {
                        result = characteristic.getStringValue(0);
                    }
                    onBLEReadListener.onReadMessage(result);
                    onBLEReadListener = null;
                }
            }
        }
        
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {     //写操作的回调
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("写入成功");
                if (onBLEWriteListenerl != null) onBLEWriteListenerl.onWrite(true);
            } else {
                if (onBLEWriteListenerl != null) onBLEWriteListenerl.onWrite(false);
            }
        }
        
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {               //数据返回的回调
            super.onCharacteristicChanged(gatt, characteristic);
            try {
                //log("value="+characteristic.getValue());
                //log("StringValue="+new String(characteristic.getValue(), "UTF-8"))
                //boolean readCharacteristic = bluetoothGatt.readCharacteristic(characteristic);
                
                //log("readCharacteristic:"+readCharacteristic);
                
                String d1 = new String(characteristic.getValue(), "UTF-8");
                if (onBLENotificationListener != null) onBLENotificationListener.onGetData(d1);
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
            listener.onWrite(false);
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
            listener.onWrite(false);
            return false;
        }
    }
    
    public boolean write(String value, OnBLEWriteListener listener) {
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
                        if (listener != null) {
                            listener.onWrite(false);
                            return;
                        }
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (listener != null) {
                    listener.onWrite(true);
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
                        if (listener != null) {
                            listener.onWrite(false);
                            return;
                        }
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (listener != null) {
                    listener.onWrite(true);
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
    
    public boolean startGetNotification(OnBLENotificationListener listener) {
        this.onBLENotificationListener = listener;

//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            boolean result = bluetoothGatt.requestMtu(512);
//            log("这是长度512："+result);
//        }
        
        
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
    
    public void end() {
        if (bluetoothGatt != null) {
            try{
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
            }catch (Exception e){}
        }
    }
}
