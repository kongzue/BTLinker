package com.kongzue.btutil.interfaces;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2018/12/11 17:22
 */
public interface OnLinkStatusChangeListener {
    
    void onStartLink();
    
    void onSuccess();
    
    void onFailed(int errorCode);
    
}
