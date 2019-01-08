package com.kongzue.btutil;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.kongzue.btutil.interfaces.BtLinkReport;
import com.kongzue.btutil.interfaces.OnBtSocketResponseListener;
import com.kongzue.btutil.interfaces.OnLinkStatusChangeListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2018/9/2 16:31
 */
public class SPPLinkUtil {
    
    public static boolean DEBUGMODE = false;                //是否打印日志
    
    public static final int ERROR_NO_DEVICE = -1;           //附近无设备
    public static final int ERROR_START_BT = -2;            //无法启动蓝牙
    public static final int ERROR_NOT_FOUND_DEVICE = -3;    //未找到目标设备
    public static final int ERROR_NOT_CONNECTED = -4;       //未建立连接
    public static final int ERROR_CONNECTED = -5;       //连接失败
    public static final int ERROR_BREAK = -50;              //连接中断
    public static final int ERROR_SOCKET_ERROR = -70;       //Socket故障
    
    private String UUIDStr = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
    private String readEndStr;                              //接收终止符（默认不设置即“\n”或“\r”或“\r\n”或“\n\r”）
    
    private BluetoothAdapter bluetooth;                     //获取本地蓝牙适配器，即蓝牙设备
    private BluetoothDevice device = null;                  //蓝牙设备
    private BluetoothSocket socket = null;                  //蓝牙通信socket
    private String btName;
    private static String btPairingCode = "1234";
    private InputStream is;                                 //输入流，用来接收蓝牙数据
    private Context context;
    
    private BtLinkReport btLinkReport;
    private OnLinkStatusChangeListener onLinkStatusChangeListener;
    private OnBtSocketResponseListener onBtSocketResponseListener;
    
    public boolean link(Context c, String btName) {
        this.btName = btName;
        this.context = c;
        if (!openBt(context)) {
            if (context != null && context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "无法打开蓝牙设备，请检查设备蓝牙是否可用";
                        loge(msg);
                        if (btLinkReport != null) btLinkReport.onError(msg);
                        if (onLinkStatusChangeListener != null)
                            onLinkStatusChangeListener.onFailed(ERROR_NO_DEVICE);
                    }
                });
            } else {
                String msg = "无法打开蓝牙设备，请检查设备蓝牙是否可用";
                loge(msg);
                if (btLinkReport != null) btLinkReport.onError(msg);
                if (onLinkStatusChangeListener != null)
                    onLinkStatusChangeListener.onFailed(ERROR_NO_DEVICE);
            }
            return false;
        } else {
            if (context != null && context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "正在打开蓝牙...";
                        log(msg);
                        if (btLinkReport != null) btLinkReport.onStatusChange(msg);
                        doOpenBluetooth(context);
                    }
                });
            } else {
                String msg = "正在打开蓝牙...";
                log(msg);
                if (btLinkReport != null) btLinkReport.onStatusChange(msg);
                doOpenBluetooth(context);
            }
        }
        
        return true;
    }
    
    private Timer linkTimer;
    
    private void doOpenBluetooth(final Context context) {
        if (linkTimer != null) linkTimer.cancel();
        linkTimer = new Timer();
        linkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (bluetooth.isEnabled() != false) {
                    doFind(context);
                    if (context != null && context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onLinkStatusChangeListener.onStartLink();
                            }
                        });
                    } else {
                        onLinkStatusChangeListener.onStartLink();
                    }
                    linkTimer.cancel();
                }
            }
        }, 1000, 1000);
    }
    
    private boolean isFinded = false;        //是否已经找到目标设备
    
    private void doFind(Context context) {
        if (context != null && context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = "正在查找设备...";
                    if (btLinkReport != null) btLinkReport.onStatusChange(msg);
                }
            });
        } else {
            String msg = "正在查找设备...";
            if (btLinkReport != null) btLinkReport.onStatusChange(msg);
        }
        
        isFinded = false;
        allDevice = new ArrayList<>();
        
        //注册接收查找到设备action接收器
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        
        //注册查找结束action接收器
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        
        //先关闭正在进行的查找
        if (bluetooth.isDiscovering()) {
            bluetooth.cancelDiscovery();
        }
        
        //开始查找目标设备
        bluetooth.startDiscovery();
    }
    
    private List<Map<String, Object>> allDevice;
    private Map<String, Object> selectDevice;
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 查找到设备action
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 得到蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                
                Map<String, Object> map = new HashMap<>();
                map.put("name", device.getName());
                map.put("address", device.getAddress());
                
                log("发现蓝牙设备：name: " + device.getName() + "   address:" + device.getAddress());
                
                allDevice.add(map);
                
                for (Map<String, Object> m : allDevice) {
                    if (btName.equals(m.get("name"))) {
                        if (!isFinded) {
                            doLink(m);
                        }
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (allDevice != null) {
                    if (allDevice.size() == 0) {
                        String msg = "附近没有任何可以连接的蓝牙设备";
                        linkStatusChange(true, ERROR_NO_DEVICE, msg);
                        
                        return;
                    } else {
                        if (!isFinded) {
                            boolean isNotHave = true;
                            for (Map<String, Object> map : allDevice) {
                                if (btName != null) {
                                    if (btName.equals(map.get("name"))) {
                                        isNotHave = false;
                                        doLink(map);
                                    }
                                }
                            }
                            if (isNotHave) {
                                String msg = "未找到要连接的目标设备";
                                linkStatusChange(true, ERROR_NOT_FOUND_DEVICE, msg);
                                return;
                            }
                        }
                    }
                }
            }
        }
    };
    
    private boolean alreadyThread = false;
    private boolean bRun = true;
    private String fmsg = "";                  //保存用数据缓存
    
    private void doLink(final Map<String, Object> map) {
        
        if (bluetooth.isDiscovering()) {
            bluetooth.cancelDiscovery();
        }
        
        isFinded = true;
        if (context != null && context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = "正在连接...";
                    log(msg);
                    if (btLinkReport != null) btLinkReport.onStatusChange(msg);
                }
            });
        } else {
            if (context != null && context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "正在连接...";
                        log(msg);
                        if (btLinkReport != null) btLinkReport.onStatusChange(msg);
                    }
                });
            } else {
                String msg = "正在连接...";
                log(msg);
                if (btLinkReport != null) btLinkReport.onStatusChange(msg);
            }
        }
        
        //故意延迟1秒执行，因为此处会卡
        Timer timer = new Timer();//实例化Timer类
        timer.schedule(new TimerTask() {
            public void run() {
                device = bluetooth.getRemoteDevice(map.get("address") + "");
                
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //开始连接
                        try {
                            //socket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                            int sdk = Build.VERSION.SDK_INT;
                            if (sdk >= 10) {
                                socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(UUIDStr));
                            } else {
                                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(UUIDStr));
                            }
                        } catch (IOException e) {
                            String msg = "创建socket失败";
                            linkStatusChange(true, ERROR_CONNECTED, msg);
                            return;
                        }
                        
                        try {
                            socket.connect();
                            String msg = "连接成功";
                            linkStatusChange(false, 0, msg);
                            checkLink();
                        } catch (IOException e) {
                            //e.printStackTrace();
                            try {
                                Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                                socket = (BluetoothSocket) m.invoke(device, 1);
                                socket.connect();
                                
                                String msg = "连接成功";
                                linkStatusChange(false, 0, msg);
                                checkLink();
                            } catch (Exception e2) {
                                try {
                                    String msg = "连接失败";
                                    linkStatusChange(true, ERROR_SOCKET_ERROR, msg);
                                    
                                    if (socket != null) {
                                        socket.close();
                                        socket = null;
                                    }
                                    return;
                                } catch (IOException ee) {
                                    String msg = "连接失败";
                                    linkStatusChange(true, ERROR_SOCKET_ERROR, msg);
                                }
                            }
                            return;
                        }
                        
                        try {
                            is = socket.getInputStream();   //得到蓝牙数据输入流
                        } catch (IOException e) {
                            String msg = "数据接收失败";
                            linkStatusChange(true, ERROR_SOCKET_ERROR, msg);
                            return;
                        }
                        
                        if (!alreadyThread) {
                            readThread.start();
                            alreadyThread = true;
                        } else {
                            bRun = true;
                        }
                    }
                }).start();
            }
        }, 1000);
    }
    
    private Timer checkTimer;
    
    private void checkLink() {
        if (checkTimer != null) checkTimer.cancel();
        checkTimer = new Timer();
        checkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!bluetooth.isEnabled()) {
                    doBreakLink();
                    checkTimer.cancel();
                } else {
                    if (!socket.isConnected()) {
                        doBreakLink();
                        checkTimer.cancel();
                    } else {
                        try {
                            byte buffer = 0;
                            OutputStream os = socket.getOutputStream();   //蓝牙连接输出流
                            os.write(buffer);
                            os.flush();
                        } catch (Exception e) {
                            doBreakLink();
                            checkTimer.cancel();
                        }
                    }
                }
            }
        }, 500, 500);
    }
    
    private void doBreakLink() {
        String msg = "连接中断";
        linkStatusChange(true, ERROR_BREAK, msg);
        close(context);
    }
    
    private StringBuffer resultMsgCache = new StringBuffer();
    
    //接收数据线程
    Thread readThread = new Thread() {
        
        public void run() {
            try {
                while (true) {
                    if (!alreadyThread)
                        return;
                    
                    if (is != null) {
                        byte[] buffer = new byte[128];
                        int count = is.read(buffer);
                        String temp = new String(buffer, 0, count, "utf-8");
                        if (cleanFlag){
                            cleanFlag = false;
                            log("cleanFlag");
                            resultMsgCache = new StringBuffer();
                        }
                        resultMsgCache.append(temp);
                        log(count + "," + resultMsgCache);
                        if (readEndStr != null) {
                            if (resultMsgCache.toString().endsWith(readEndStr)) {
                                btSocketResponse();
                            }
                        } else {
                            if (resultMsgCache.toString().endsWith("\n") || resultMsgCache.toString().endsWith("\r") || resultMsgCache.toString().endsWith("\r\n") || resultMsgCache.toString().endsWith("\n\r")) {
                                btSocketResponse();
                            }
                        }
                    }
                }
            } catch (Exception e) {
            
            }
        }
    };
    
    private boolean cleanFlag = false;
    
    private void btSocketResponse() {
        if (context != null && context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (btLinkReport != null)
                        btLinkReport.onGetData(resultMsgCache.toString());
                    if (onBtSocketResponseListener != null)
                        onBtSocketResponseListener.onResponse(resultMsgCache.toString());
                
                }
            });
        } else {
            if (btLinkReport != null)
                btLinkReport.onGetData(resultMsgCache.toString());
            if (onBtSocketResponseListener != null)
                onBtSocketResponseListener.onResponse(resultMsgCache.toString());
        }
        cleanFlag = true;
    }
    
    public void send(String text) {
        int i = 0;
        int n = 0;
        if (socket == null) {
            String msg = "未连接";
            linkStatusChange(true, ERROR_NOT_CONNECTED, msg);
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();   //蓝牙连接输出流
            byte[] bos = text.getBytes();
            for (i = 0; i < bos.length; i++) {
                if (bos[i] == 0x0a) n++;
            }
            byte[] bos_new = new byte[bos.length + n];
            n = 0;
            for (i = 0; i < bos.length; i++) { //手机中换行为0a,将其改为0d 0a后再发送
                if (bos[i] == 0x0a) {
                    bos_new[n] = 0x0d;
                    n++;
                    bos_new[n] = 0x0a;
                } else {
                    bos_new[n] = bos[i];
                }
                n++;
            }
            
            os.write(bos);
            log(">>>send:" + text);
        } catch (IOException e) {
            if (DEBUGMODE) e.printStackTrace();
        }
    }
    
    private boolean openBt(Context context) {
        bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备
        if (bluetooth == null) {
            loge("无法打开蓝牙");
            if (onLinkStatusChangeListener != null)
                onLinkStatusChangeListener.onFailed(ERROR_START_BT);
            return false;
        }
        //启动蓝牙
        if (bluetooth.isEnabled() == false) {
            bluetooth.enable();
        }
        return true;
    }
    
    public SPPLinkUtil setBtLinkReport(BtLinkReport btLinkReport) {
        this.btLinkReport = btLinkReport;
        return this;
    }
    
    public SPPLinkUtil setOnLinkStatusChangeListener(OnLinkStatusChangeListener onLinkStatusChangeListener) {
        this.onLinkStatusChangeListener = onLinkStatusChangeListener;
        return this;
    }
    
    public SPPLinkUtil setOnBtSocketResponseListener(OnBtSocketResponseListener onBtSocketResponseListener) {
        this.onBtSocketResponseListener = onBtSocketResponseListener;
        return this;
    }
    
    public SPPLinkUtil setUUID(String UUID) {
        this.UUIDStr = UUID;
        return this;
    }
    
    public SPPLinkUtil setReadEndStr(String readEndStr) {
        this.readEndStr = readEndStr;
        return this;
    }
    
    public static void setBtPairingCode(String btPairingCode) {
        SPPLinkUtil.btPairingCode = btPairingCode;
    }
    
    public void close(Context context) {
        if (context == null) return;
        if (linkTimer != null) linkTimer.cancel();
        if (checkTimer != null) checkTimer.cancel();
        try {
            context.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        bRun = false;
        
        try {
            if (readThread != null) {
                readThread.interrupt();
                readThread = null;
                alreadyThread = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            if (is != null) {
                is.close();
                is = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            if (bluetooth != null) {
                bluetooth.cancelDiscovery();
                bluetooth.disable();
                bluetooth = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            fmsg = "";
            
            isFinded = false;
            btLinkReport = null;
            device = null;
            btName = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice) throws Exception {
        if (btPairingCode == null || btPairingCode.isEmpty()) {
            return false;
        }
        try {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(
                    btDevice,
                    new Object[]
                            {btPairingCode.getBytes()}
            );
            Log.e("returnValue", "" + returnValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
        
    }
    
    private void log(String msg) {
        if (DEBUGMODE) Log.i(">>>", "BTLinkUtil:" + msg);
    }
    
    private static void loge(String msg) {
        if (DEBUGMODE) Log.e(">>>", "BTLinkUtil:" + msg);
    }
    
    private void linkStatusChange(final boolean isFailed, final int errorId, final String msg) {
        if (context != null && context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loge(msg);
                    if (isFailed) {
                        if (btLinkReport != null) btLinkReport.onError(msg);
                        if (onLinkStatusChangeListener != null)
                            onLinkStatusChangeListener.onFailed(errorId);
                    } else {
                        if (btLinkReport != null)
                            btLinkReport.onStatusChange(msg);
                        if (onLinkStatusChangeListener != null)
                            onLinkStatusChangeListener.onSuccess();
                    }
                }
            });
        } else {
            loge(msg);
            if (isFailed) {
                if (btLinkReport != null) btLinkReport.onError(msg);
                if (onLinkStatusChangeListener != null)
                    onLinkStatusChangeListener.onFailed(errorId);
            } else {
                if (btLinkReport != null)
                    btLinkReport.onStatusChange(msg);
                if (onLinkStatusChangeListener != null)
                    onLinkStatusChangeListener.onSuccess();
            }
        }
    }
}
