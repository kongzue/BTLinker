package com.kongzue.btutil.interfaces;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2018/9/2 16:44
 */
public interface BtLinkReport {
    void onStatusChange(String message);
    
    void onError(String message);
    
    void onGetData(String datas);
}
