package com.itsure.balance.server.impl;

import com.itsure.balance.BalanceUpdateProvider;
import com.itsure.balance.ServerData;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkBadVersionException;
import org.I0Itec.zkclient.exception.ZkException;
import org.apache.zookeeper.data.Stat;

/**
 * @author itsure
 * @date 2019/07/18
 */
public class DefaultBalanceUpdateProvider implements BalanceUpdateProvider {

    /**
     * 注册的服务器节点路径
     */
    private String serverPath;

    private ZkClient zkClient;

    public DefaultBalanceUpdateProvider(String serverPath, ZkClient zkClient) {
        this.serverPath = serverPath;
        this.zkClient = zkClient;
    }

    @Override
    public boolean addBalance(Integer step) {
        Stat stat = new Stat();
        ServerData serverData = null;
        try {
            serverData =  zkClient.readData(serverPath, stat);
            serverData.setBalance(serverData.getBalance() + step);
            zkClient.writeData(serverPath, serverData, stat.getVersion());
        } catch (ZkBadVersionException e) {

        } catch (ZkException e ){
            return false;
        }
        return true;
    }

    @Override
    public boolean reduceBalance(Integer step) {
        Stat stat = new Stat();
        ServerData serverData;
        try {
            serverData =  zkClient.readData(serverPath, stat);
            final int currentBalance = serverData.getBalance();
            serverData.setBalance(currentBalance > step ? serverData.getBalance() - step : 0);
            zkClient.writeData(serverPath, serverData, stat.getVersion());
        } catch (ZkBadVersionException e) {

        } catch (ZkException e ){
            return false;
        }
        return true;
    }
}
