# BTLinker
蓝牙连接封装库，适用于智能硬件蓝牙通讯，使用 SPP 服务（稍后会支持BLE）。

<a href="https://github.com/kongzue/BTLinker/">
<img src="https://img.shields.io/badge/BTLinker-1.0.7-green.svg" alt="Kongzue BTLinker">
</a>
<a href="https://bintray.com/myzchh/maven/BTLinker/1.0.7/link">
<img src="https://img.shields.io/badge/Maven-1.0.7-blue.svg" alt="Maven">
</a>
<a href="http://www.apache.org/licenses/LICENSE-2.0">
<img src="https://img.shields.io/badge/License-Apache%202.0-red.svg" alt="License">
</a>
<a href="http://www.kongzue.com">
<img src="https://img.shields.io/badge/Homepage-Kongzue.com-brightgreen.svg" alt="Homepage">
</a>

Demo预览图如下：

![BTLinker](https://github.com/kongzue/Res/raw/master/app/src/main/res/mipmap-xxxhdpi/img_btlinker.jpg)

Demo下载：https://fir.im/BTLinker

## ⚠前言

目前已支持 SPP 和 BLE 模式，对应使用 SPPLinkUtil 和 BLELinkUtil 工具类。

本框架无需做太多复杂操作，开关蓝牙也都是自动的，您只需要连接需要的设备，进行相关的操作即可。

使用本框架需要一定的蓝牙结构知识，对于 SPP 和 BLE 通讯的结构组成，本文不再赘述，要了解请自行搜索资料。

### 关于蓝牙2.0（SPP）的一些说明

因蓝牙 Socket 存在天坑，数据包可能发生粘包情况，请您与硬件端约定一个消息结束符（默认为各种回车符） 为结尾（默认\r\n），来代表此条消息结束，否则软件层面上无法得知消息结束会继续等待后续的消息导致无法通过监听器吐出任何数据。

偶尔发送数据包会存在丢包的风险导致硬件端未接受到指令，建议发送指令后服务端返回个约定的执行成功指令给客户端，若没有建议客户端重复发送之前的指令直到成功。

### 关于蓝牙4.0（BLE）的一些说明

由于 Android 底层限制，默认发送、接收的消息内容都被限制在20字节以内，导致收发消息出现断断续续或只有前20字的情况，本框架会在完成通道连接后发送最大512字节的数据包申请，若无效，请与硬件开发联系支持更大的数据包。

要发送20字以上数据请使用 writeBIG(...) 方法，该方法的实现原理是将要发送的数据裁剪为多个20字节的数据包，每隔50毫秒发送一次，请与硬件端商量进行数据组包。

由于 BLE 接收的消息可能存在粘包情况（上下两条消息发生断续或首尾相接的问题），为保证读取到的消息完整性，建议与设备端开发约定消息头及消息尾的字符，默认以“$”开头、以“\r\n”结尾为一条消息，可通过阅读详细文档设置修改，如果未读取到消息尾，回调方法可能发生不执行情况，请知悉。

## 使用方法
1) 从 Maven 仓库或 jCenter 引入：
Maven仓库：
```
<dependency>
  <groupId>com.kongzue.smart</groupId>
  <artifactId>btutil</artifactId>
  <version>1.0.7</version>
  <type>pom</type>
</dependency>
```
Gradle：
在dependencies{}中添加引用：
```
implementation 'com.kongzue.smart:btutil:1.0.7'
```

## 关于权限
您需要申请蓝牙权限后才可以正常使用

主要权限：
```
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

需要申请：
```
Manifest.permission.ACCESS_COARSE_LOCATION,
Manifest.permission.ACCESS_FINE_LOCATION
```

若不声明或申请权限，可能导致无法查找到要连接的目标蓝牙，或无法正常使用功能。

### 蓝牙 2.0 SPP

1) 初始化 SPPLinkUtil
```
private SPPLinkUtil spplinkUtil;
//...
SPPLinkUtil = new SPPLinkUtil();

//UUID 是 SPP 服务的 UUID号，如果使用 HC-06 蓝牙主板则无需修改。
SPPLinkUtil.setUUID(uuid)

//设置状态监听
.setOnLinkStatusChangeListener(new OnLinkStatusChangeListener() {
    @Override
    public void onStartLink() {
        //开始连接
    }

    @Override
    public void onSuccess() {
        //连接完成
    }

    @Override
    public void onFailed(int errorCode) {
        //连接错误，详见《连接错误代码》章节
    }
})

//设置设备回传数据监听
.setOnBtSocketResponseListener(new OnBtSocketResponseListener() {
    @Override
    public void onResponse(String msg) {
        //msg即设备返回App的数据
    }
});

//开始连接，若 context 是一个 Activity 则回调监听器会自动返回主线程操作。
spplinkUtil.link(context, 蓝牙名称);
```

2) 其他方法
```
//发送消息给设备
spplinkUtil.send(String);

//别忘记在 Activity 退出后结束事务
@Override
protected void onDestroy() {
    spplinkUtil.close(me);
    super.onDestroy();
}

//开启日志打印（请注意大小写）
SPPLinkUtil.DEBUGMODE = true;

//修改配对码（请注意大小写）
SPPLinkUtil.setBtPairingCode("666666");
```

3) 连接错误代码

字段 | 值 | 含义
---|---|---
ERROR_NO_DEVICE | -1 | 附近无设备
ERROR_START_BT | -2 | 无法启动蓝牙
ERROR_NOT_FOUND_DEVICE | -3 | 未找到目标设备
ERROR_NOT_CONNECTED | -4 | 未建立连接
ERROR_SOCKET_ERROR | -70 | Socket故障
ERROR_BREAK | -50 | 连接中断

4) 回传终止符（重要）

请与您的硬件开发者商定一个回传终止符。

回传终止符应当位于硬件设备回传消息的末尾，它用于标记此次指令结束，因传输方式为 socket，鉴于 socket 粘包的特性，若不设置回传终止符则无法确定此次消息结束。

默认的回传接收终止符为“\n”或“\r”或“\r\n”或“\n\r”，也就是说，如果硬件向 App 发送的消息末尾没有换行，则 SPPLinkUtil 不会认为本条消息结束，继续处于等待状态。

只有接收到回传终止符，OnBtSocketResponseListener 中的回调才会有效，将之前缓存的消息传送出来。

### 蓝牙 4.0 BLE
>初始化BLE组件
```
bleLinkUtil = new BLELinkUtil();
```

使用步骤一般为 **开启蓝牙 → 扫描并连接设备 → 连接成功后设置服务通道扫描监听器回调**

>开启蓝牙
```
bleLinkUtil.openBluetooth(new BluetoothOpenListener() {
    @Override
    public void onResponse(boolean isSuccess, int errorCode, String errorMsg) {
    
    }
}
```

>初始化后就需要寻找附近的BLE设备了：
```
//设置查询监听（寻找附近的BLE设备）
bleLinkUtil.doScan(new OnBLEScanListener() {
    @Override
    public BluetoothDevice onFindDevice(BluetoothDevice device) {
    
    }
    
    @Override
    public void getAllDevice(List<BluetoothDevice> devices) {
    
    }
    
    @Override
    public void onStop() {
    
    }
);
```
>设置监听器 setOnBLEScanListener，其中有两个接口，一个是 getAllDevice(List<BluetoothDevice> devices) 会重复性的返回所有当前已找到的设备列表，适用于以此制作 Adapter 来显示 ListView 列表。

另一个方法 onFindDevice(BluetoothDevice device) 会在每查找到一个新设备时返回，不会重复，可以进行判断后直接 return 该设备直接连接该设备，适用于快速完成设备连接的业务流程。

注：直接 return 方式连接前需要手动设置 bleLinkUtil.setOnBLEFindServiceListener(...) 连接状态回调监听器。

>另外要手动进行设备连接，可以使用以下方式：
```
bleLinkUtil.linkDevice(devices.get(position), new OnBLEFindServiceListener() {
    @Override
    public void onLink(boolean isSuccess, final List<BluetoothGattService> services) {
        //对 services 进行处理，注意此时为异步线程。
    }
});
```
返回的 List<BluetoothGattService> services 为连接成功后该 BLE 设备提供的服务，可以通过 services.getUuid() 获取其 UUID，可以通过 services.getCharacteristics() 获取其通道。

获取到的 Characteristics 对象，可使用 characteristics.getUuid() 判断其 UUID，判断是否为自己需要的通道。

对于已知 Service 和 Characteristics 的 UUID 的情况下，也可通过以下方法直接获取通道：
```
BluetoothGattCharacteristic characteristic = bleLinkUtil.getCharacteristic(serviceUUID, childUUID);
```

也可以直接设置接下来要操作的通道：
```
bleLinkUtil.setUUID(serviceUUID, childUUID);
```

>获取到通道后，就可以进行主要的操作了：

1) 进行消息读取：
```
bleLinkUtil.read(new OnBLEReadListener() {
    @Override
    public void onReadMessage(String msg) {
        
    }
});
```

2) 进行消息的写入：
```
String text = editWrite.getText().toString().trim();
if (!text.isEmpty()) {
    bleLinkUtil.write(text, new OnBLEWriteListener() {
        @Override
        public boolean onWrite(boolean isSuccess,String returnMsg) {
            if (isSuccess) {
                Toast.makeText(me, "发送成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(me, "发送失败", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });
}
```

OnBLEWriteListener 中回调参数 returnMsg 是在发送指令后，通过通道的 read 方式读取到的值，若该通道启用了通知，那么 OnBLEWriteListener 会被多次回调，且通知消息也会在 returnMsg 中回传，直到您执行 return true; 来代表您已处理回传的消息。

之所以这么做，原因是部分硬件开发习惯性通过“通知”来回传指令消息，但通知是不断接收的，同时还会有其他通知回传，因此需要不停的判断是否读取到回传的“通知”成功后通过 return true 来停止判断。

大于20字节消息的写入：
```
String text = editWrite.getText().toString().trim();
if (!text.isEmpty()) {
    bleLinkUtil.writeBIG(text, new OnBLEWriteListener() {
        @Override
        public boolean onWrite(boolean isSuccess,String returnMsg) {
            if (isSuccess) {
                Toast.makeText(me, "发送成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(me, "发送失败", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });
}
```

3) 进行通知消息的订阅：

所谓通知消息，即申请订阅该通知后，设备端会不断通过蓝牙给手机端传送数据。
```
bleLinkUtil.startGetNotification(new OnBLENotificationListener() {
    @Override
    public void onGetData(String data) {
        
    }
});
```

额外的，可以通过以下代码订阅其他通道的通知：
```
//SERVICE_UUID 为服务的UUID，NOTIFY_CHARACTERISTIC_UUID 为指定通知管道的 UUID
bleLinkUtil.startGetNotification(SERVICE_UUID, NOTIFY_CHARACTERISTIC_UUID, new OnBLENotificationListener() {
    @Override
    public void onGetData(String data) {
        log("接收到通知: " + data);
    }
});
```

4) 额外方法

>设置消息头：
```
BleLinkUtil.messageStart = "$";
```

>设置消息尾：
```
BleLinkUtil.messageEnd = "\r\n";
```

>停止搜寻设备：
```
bleLinkUtil.stopScan();
```

>结束事务（包括停止一切活动） **⚠ 建议放在 Activity 的 onDestroy() 事件中执行**
```
bleLinkUtil.cancel();
```

>判断通道属性：
```
//判断通道可读
bleLinkUtil.ifCharacteristicReadable(Characteristic);

//判断通道可写
bleLinkUtil.ifCharacteristicWritable(Characteristic);

//判断通道是否通知
bleLinkUtil.ifCharacteristicNotifiable(Characteristic);

//是否开启 DEBUG 模式（开启后本工具会持续打印日志信息用于辅助判断流程是否存在问题）
bleLinkUtil.DEBUGMODE = true;

```

## 开源协议
```
Copyright Kongzue BTLinker

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 更新日志
v1.0.7:
- 完善和修复 bug；

v1.0.6.1：
- 新增 BLE 工具类消息头、消息尾设置；

v1.0.6.0：
- 更新 BLE 工具类并修复了一些 bug；

v1.0.5：
- 修复了一些连接逻辑 bug；

v1.0.4：
- 解决蓝牙 4.0（BLE）传输数据20字节限制的问题。

v1.0.3：
- 修复了一些bug；

v1.0.2：
- 修改逻辑，并将目前 LinkUtil 改名为 SPPLinkUtil；
- 修复了一些bug；

v1.0.1：
- 新增连接后的断开校验；
- 修改建立连接逻辑，连接更稳定；

v1.0.0：
- 首次上传；