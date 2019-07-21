package com.itsure.balance;

/**
 * @author itsure
 * @date 2019/07/18
 */
public interface RegisterProvider {

    public void regist(Object context) throws Exception;

    public void unRegist(Object context) throws Exception;

}
