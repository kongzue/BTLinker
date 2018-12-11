package com.kongzue.btlinker;

import android.Manifest;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kongzue.baseframework.BaseActivity;
import com.kongzue.baseframework.interfaces.Layout;
import com.kongzue.baseframework.util.JumpParameter;
import com.kongzue.baseframework.util.OnPermissionResponseListener;
import com.kongzue.btutil.LinkUtil;
import com.kongzue.btutil.interfaces.OnBtSocketResponseListener;
import com.kongzue.btutil.interfaces.OnLinkStatusChangeListener;

@Layout(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    
    private LinkUtil linkUtil;
    
    private String[] arraysStr = {"\\r\\n", "\\n\\r", "\\n", "\\r"};
    private String[] arrays = {"\r\n", "\n\r", "\n", "\r"};
    private String enterStr = "\r\n";
    
    private EditText editUuid;
    private EditText editName;
    private Button btnLink;
    private LinearLayout boxConnected;
    private EditText editSend;
    private Spinner spEnter;
    private Button btnSend;
    private TextView txtGetMsg;
    
    @Override
    public void initViews() {
        editUuid = findViewById(R.id.edit_uuid);
        editName = findViewById(R.id.edit_name);
        btnLink = findViewById(R.id.btn_link);
        boxConnected = findViewById(R.id.box_connected);
        editSend = findViewById(R.id.edit_send);
        spEnter = findViewById(R.id.sp_enter);
        btnSend = findViewById(R.id.btn_send);
        txtGetMsg = findViewById(R.id.txt_getMsg);
    }
    
    @Override
    public void initDatas(JumpParameter paramer) {
        requestPermission(new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, new OnPermissionResponseListener() {
            @Override
            public void onSuccess(String[] permissions) {
            
            }
            
            @Override
            public void onFail() {
                toast("请允许权限");
                finish();
            }
        });
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_spinner_item,
                arraysStr
        );
        
        spEnter.setAdapter(adapter);
        
        LinkUtil.DEBUGMODE = true;
        linkUtil = new LinkUtil();
        
    }
    
    private ProgressDialog progressDialog;
    
    @Override
    public void setEvents() {
        spEnter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enterStr = arrays[position];
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            
            }
        });
        
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                String uuid = editUuid.getText().toString().trim();
                String pairingCode = "1234";
                
                if (isNull(name)) {
                    toast("蓝牙名称不能为空");
                    return;
                }
                
                linkUtil.setUUID(uuid)
                        .setOnLinkStatusChangeListener(new OnLinkStatusChangeListener() {
                            @Override
                            public void onStartLink() {
                                progressDialog = ProgressDialog.show(MainActivity.this, "连接中", "请稍候...");
                            }
                            
                            @Override
                            public void onSuccess() {
                                progressDialog.dismiss();
                                editName.setEnabled(false);
                                editUuid.setEnabled(false);
                                btnLink.setEnabled(false);
                                boxConnected.setVisibility(View.VISIBLE);
                            }
                            
                            @Override
                            public void onFailed(final int errorCode) {
                                progressDialog.dismiss();
                                toast("错误：" + errorCode);
                            }
                        })
                        .setOnBtSocketResponseListener(new OnBtSocketResponseListener() {
                            @Override
                            public void onResponse(String msg) {
                                log(">>>"+msg);
                                txtGetMsg.setText(msg);
                            }
                        });
                
                linkUtil.link(me, name);
            }
        });
        
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editSend.getText().toString().trim();
                if (isNull(msg)) {
                    toast("请输入指令");
                    return;
                }
                linkUtil.send(msg + enterStr);
            }
        });
        
    }
    
    @Override
    protected void onDestroy() {
        linkUtil.close(me);
        super.onDestroy();
    }
    
}
