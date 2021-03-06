package com.kongzue.btlinker;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kongzue.btutil.BLELinkUtil;
import com.kongzue.btutil.interfaces.OnBLEFindServiceListener;
import com.kongzue.btutil.interfaces.OnBLENotificationListener;
import com.kongzue.btutil.interfaces.OnBLEReadListener;
import com.kongzue.btutil.interfaces.OnBLEScanListener;
import com.kongzue.btutil.interfaces.OnBLEWriteListener;
import com.kongzue.btutil.util.GattAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BLEActivity extends AppCompatActivity {
    
    public static final String AUTO_LINK_DEVICE_MAC = "D8:B0:4C:CC:70:EF";

    private LinearLayout boxDevice;
    private Button btnLink;
    private ListView listDevice;
    private LinearLayout boxService;
    private ListView listService;
    private LinearLayout boxLinked;
    private TextView txtName;
    private TextView txtServiceUUID;
    private TextView txtChildUUID;
    private Button btnSelect;
    private Button btnRead;
    private Button btnNotify;
    private TextView txtRead;
    private LinearLayout boxWrite;
    private EditText editWrite;
    private Button btnWrite;

    private ProgressDialog progressDialog;
    
    private BLELinkUtil bleLinkUtil;
    private BLEActivity me;
    private List<Map<String, String>> deviceList;
    private List<BluetoothDevice> devices;
    private SimpleAdapter deviceSimpleAdapter;
    
    private List<Map<String, String>> serviceList;
    private SimpleAdapter serviceSimpleAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        
        me = this;

        boxDevice = (LinearLayout) findViewById(R.id.box_device);
        btnLink = (Button) findViewById(R.id.btn_link);
        listDevice = (ListView) findViewById(R.id.list_device);
        boxService = (LinearLayout) findViewById(R.id.box_service);
        listService = (ListView) findViewById(R.id.list_service);
        boxLinked = (LinearLayout) findViewById(R.id.box_linked);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtServiceUUID = (TextView) findViewById(R.id.txt_serviceUUID);
        txtChildUUID = (TextView) findViewById(R.id.txt_childUUID);
        btnSelect = (Button) findViewById(R.id.btn_select);
        btnRead = (Button) findViewById(R.id.btn_read);
        btnNotify = (Button) findViewById(R.id.btn_notify);
        txtRead = (TextView) findViewById(R.id.txt_read);
        boxWrite = (LinearLayout) findViewById(R.id.box_write);
        editWrite = (EditText) findViewById(R.id.edit_write);
        btnWrite = (Button) findViewById(R.id.btn_write);
        
        BLELinkUtil.DEBUGMODE = true;
        
        deviceList = new ArrayList<>();
        
        bleLinkUtil = new BLELinkUtil(me);
        
        bleLinkUtil.setOnBLEScanListener(new OnBLEScanListener() {
            
            @Override
            public BluetoothDevice onFindDevice(BluetoothDevice device) {
                //自动连接方法：
                //if (device.getAddress().equals(AUTO_LINK_DEVICE_MAC)) {
                //    return device;
                //}
                return null;
            }
            
            @Override
            public void getAllDevice(List<BluetoothDevice> devices) {
                //查询所有附近BLE蓝牙设备并更新到列表
                me.devices = devices;
                deviceList.clear();
                for (BluetoothDevice device : devices) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", device.getName());
                    map.put("address", device.getAddress());
                    deviceList.add(map);
                }
                refreshDeviceList();
            }
    
            @Override
            public void onStop() {
                //超时或停止搜寻
            
            }
    
        });
        
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (bleLinkUtil.isScanning()) {
                //    btnLink.setText("开始搜索");
                //    bleLinkUtil.stopScan();
                //} else {
                //    btnLink.setText("停止搜索");
                //    bleLinkUtil.doScan(null);
                //}
                progressDialog = ProgressDialog.show(me, "连接中", "请稍候...");
                bleLinkUtil.linkDevice("08:7C:BE:88:F3:04",new OnBLEFindServiceListener() {
                    @Override
                    public void onLink(boolean isSuccess, final List<BluetoothGattService> services) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                boxDevice.setVisibility(View.GONE);
                                boxService.setVisibility(View.VISIBLE);
                
                                serviceList = new ArrayList<>();
                                for (int i = 0; i < services.size(); i++) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("title", "服务：" + GattAttributes.lookup(services.get(i).getUuid().toString(), ""));
                                    map.put("uuid", services.get(i).getUuid().toString());
                                    map.put("service", "");
                                    serviceList.add(map);
                    
                                    List<BluetoothGattCharacteristic> listGattCharacteristic = services.get(i).getCharacteristics();
                                    for (int j = 0; j < listGattCharacteristic.size(); j++) {
                                        BluetoothGattCharacteristic gattCharacteristic = listGattCharacteristic.get(j);
                        
                                        map = new HashMap<>();
                                        map.put("title", "成员：" + GattAttributes.lookup(listGattCharacteristic.get(j).getUuid().toString(), "") + bleLinkUtil.getPorperties(me, gattCharacteristic));
                                        map.put("uuid", listGattCharacteristic.get(j).getUuid().toString());
                                        map.put("service", services.get(i).getUuid().toString());
                                        serviceList.add(map);
                                    }
                                    refreshServiceList();
                                }
                            }
                        });
                    }
                });
            }
        });
        
        listDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                btnLink.setEnabled(false);
                
                progressDialog = ProgressDialog.show(me, "连接中", "请稍候...");
                bleLinkUtil.linkDevice(devices.get(position), new OnBLEFindServiceListener() {
                    @Override
                    public void onLink(boolean isSuccess, final List<BluetoothGattService> services) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                boxDevice.setVisibility(View.GONE);
                                boxService.setVisibility(View.VISIBLE);
                                
                                serviceList = new ArrayList<>();
                                for (int i = 0; i < services.size(); i++) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("title", "服务：" + GattAttributes.lookup(services.get(i).getUuid().toString(), ""));
                                    map.put("uuid", services.get(i).getUuid().toString());
                                    map.put("service", "");
                                    serviceList.add(map);
                                    
                                    List<BluetoothGattCharacteristic> listGattCharacteristic = services.get(i).getCharacteristics();
                                    for (int j = 0; j < listGattCharacteristic.size(); j++) {
                                        BluetoothGattCharacteristic gattCharacteristic = listGattCharacteristic.get(j);
                                        
                                        map = new HashMap<>();
                                        map.put("title", "成员：" + GattAttributes.lookup(listGattCharacteristic.get(j).getUuid().toString(), "") + bleLinkUtil.getPorperties(me, gattCharacteristic));
                                        map.put("uuid", listGattCharacteristic.get(j).getUuid().toString());
                                        map.put("service", services.get(i).getUuid().toString());
                                        serviceList.add(map);
                                    }
                                    refreshServiceList();
                                }
                            }
                        });
                    }
                });
                
            }
        });
        
        listService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = serviceList.get(position).get("title");
                String serviceUUID = serviceList.get(position).get("service");
                String childUUID = serviceList.get(position).get("uuid");
                if (!serviceUUID.isEmpty()) {
                    bleLinkUtil.setUUID(serviceUUID, childUUID);
                    boxService.setVisibility(View.GONE);
                    boxLinked.setVisibility(View.VISIBLE);
                    txtRead.setText("");
                    
                    txtName.setText(name);
                    txtServiceUUID.setText(serviceUUID);
                    txtChildUUID.setText(childUUID);
                    
                    BluetoothGattCharacteristic characteristic = bleLinkUtil.getCharacteristic(serviceUUID, childUUID);
                    if (bleLinkUtil.ifCharacteristicWritable(characteristic)){
                        boxWrite.setVisibility(View.VISIBLE);
                    }else{
                        boxWrite.setVisibility(View.GONE);
                    }
                }
            }
        });
        
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boxService.setVisibility(View.VISIBLE);
                boxLinked.setVisibility(View.GONE);
            }
        });
        
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleLinkUtil.read(new OnBLEReadListener() {
                    @Override
                    public void onReadMessage(String msg) {
                        Log.i(">>>", "onReadMessage: "+msg);
                        txtRead.setText(msg);
                    }
                });
            }
        });
        
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editWrite.getText().toString().trim();
                if (!text.isEmpty()) {
                    bleLinkUtil.write(text + "\r\n", new OnBLEWriteListener() {
                        @Override
                        public boolean onWrite(boolean isSuccess,String readStr) {
                            Log.e(">>>", "onWrite: "+readStr );
                            if (isSuccess) {
                                Toast.makeText(me, "发送成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(me, "发送失败", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                }
            }
        });

        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bleLinkUtil.startGetNotification("0003cdd0-0000-1000-8000-00805f9b0131","0003cdd1-0000-1000-8000-00805f9b0131",new OnBLENotificationListener() {
                    @Override
                    public void onGetData(String data) {
                        Log.i(">>>", "onReadMessage: "+data);
                        txtRead.setText(txtRead.getText().toString()+"\n"+data);
                    }
                });
                Toast.makeText(me,"已开启通知",Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void refreshServiceList() {
        if (serviceSimpleAdapter == null) {
            serviceSimpleAdapter = new SimpleAdapter(me, serviceList, R.layout.item_device,
                                                     new String[]{"title", "uuid"},
                                                     new int[]{R.id.txt_name, R.id.txt_address}
            );
            listService.setAdapter(serviceSimpleAdapter);
        } else {
            serviceSimpleAdapter.notifyDataSetChanged();
        }
    }
    
    private void refreshDeviceList() {
        if (deviceSimpleAdapter == null) {
            deviceSimpleAdapter = new SimpleAdapter(me, deviceList, R.layout.item_device,
                                                    new String[]{"name", "address"},
                                                    new int[]{R.id.txt_name, R.id.txt_address}
            );
            listDevice.setAdapter(deviceSimpleAdapter);
        } else {
            deviceSimpleAdapter.notifyDataSetChanged();
        }
    }
    
    @Override
    public void onBackPressed() {
        if (boxLinked.getVisibility() == View.VISIBLE) {
            btnSelect.callOnClick();
        } else {
            super.onBackPressed();
        }
    }
}
