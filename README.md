# BTLinker
蓝牙连接封装库，适用于智能硬件蓝牙通讯。

<a href="https://github.com/kongzue/BTLinker/">
<img src="https://img.shields.io/badge/BTLinker-1.0.1-green.svg" alt="Kongzue BTLinker">
</a>
<a href="https://bintray.com/myzchh/maven/BTLinker/1.0.1/link">
<img src="https://img.shields.io/badge/Maven-1.0.1-blue.svg" alt="Maven">
</a>
<a href="http://www.apache.org/licenses/LICENSE-2.0">
<img src="https://img.shields.io/badge/License-Apache%202.0-red.svg" alt="License">
</a>
<a href="http://www.kongzue.com">
<img src="https://img.shields.io/badge/Homepage-Kongzue.com-brightgreen.svg" alt="Homepage">
</a>

Demo预览图如下：

![BTLinker](https://github.com/kongzue/Res/raw/master/app/src/main/res/mipmap-xxxhdpi/img_btlinker.jpg)

### 使用方法
1) 从 Maven 仓库或 jCenter 引入：
Maven仓库：
```
<dependency>
  <groupId>com.kongzue.smart</groupId>
  <artifactId>btutil</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```
Gradle：
在dependencies{}中添加引用：
```
implementation 'com.kongzue.smart:btutil:1.0.1'
```

2) 初始化 LinkUtil
```
private LinkUtil linkUtil;
//...
linkUtil = new LinkUtil();

//UUID 是 SPP 服务的 UUID号，如果使用 HC-06 蓝牙主板则无需修改。
linkUtil.setUUID(uuid)

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
linkUtil.link(context, 蓝牙名称);
```

3) 其他方法
```
//发送消息给设备
linkUtil.send(String);

//别忘记在 Activity 退出后结束事务
@Override
protected void onDestroy() {
    linkUtil.close(me);
    super.onDestroy();
}

//开启日志打印（请注意大小写）
LinkUtil.DEBUGMODE = true;

//修改配对码（请注意大小写）
LinkUtil.setBtPairingCode("666666");
```

## 连接错误代码
字段 | 值 | 含义
---|---|---
ERROR_NO_DEVICE | -1 | 附近无设备
ERROR_START_BT | -2 | 无法启动蓝牙
ERROR_NOT_FOUND_DEVICE | -3 | 未找到目标设备
ERROR_NOT_CONNECTED | -4 | 未建立连接
ERROR_SOCKET_ERROR | -70 | Socket故障
ERROR_BREAK | -50 | 连接中断

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

## 回传终止符（重要）
请与您的硬件开发者商定一个回传终止符。

回传终止符应当位于硬件设备回传消息的末尾，它用于标记此次指令结束，因传输方式为 socket，鉴于 socket 粘包的特性，若不设置回传终止符则无法确定此次消息结束。

默认的回传接收终止符为“\n”或“\r”或“\r\n”或“\n\r”，也就是说，如果硬件向 App 发送的消息末尾没有换行，则 LinkUtil 不会认为本条消息结束，继续处于等待状态。

只有接收到回传终止符，OnBtSocketResponseListener 中的回调才会有效，将之前缓存的消息传送出来。

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
v1.0.1：
- 新增连接后的断开校验；
- 修改建立连接逻辑，连接更稳定；

v1.0.0：
- 首次上传；