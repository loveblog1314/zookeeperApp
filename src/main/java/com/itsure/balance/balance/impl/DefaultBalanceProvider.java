package com.itsure.balance.balance.impl;

import com.itsure.balance.AbstractBalanceProvider;
import com.itsure.balance.ServerData;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author itsure
 * @date 2019/07/18
 */
public class DefaultBalanceProvider extends AbstractBalanceProvider {
    /**
     * 服务器地址
     */
    private final String zkAddress;
    /**
     * 服务器注册的节点
     */
    private final String serverPath;

    private ZkClient zkClient;

    public DefaultBalanceProvider(String zkAddress, String serverPath) {
        this.zkAddress = zkAddress;
        this.serverPath = serverPath;
        this.zkClient = new ZkClient(zkAddress, 5000, 5000, new SerializableSerializer());
    }

    @Override
    protected Object balanceAlgorithm(List items) {
        if (items.size() > 0) {
            Collections.sort(items);
            return  items.get(0);
        }
        return null;
    }

    @Override
    protected List getBalanceItems() {
        List<ServerData> serverDataList = new ArrayList<>();
        List<String> serverList = zkClient.getChildren(serverPath);
        for (int i = 0; i < serverList.size(); i ++) {
            ServerData serverData = zkClient.readData(serverPath+"/"+serverList.get(i));
            serverDataList.add(serverData);

        }
        return serverDataList;
    }
}
