package com.kongzue.btutil.interfaces;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2018/9/2 16:44
 */
public interface BtLinkReport {
    public void onStatusChange(String message);
    
    public void onError(String message);
    
    public void onGetData(String datas);
}
