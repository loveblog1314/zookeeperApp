package com.itsure.balance.server.impl;

import com.itsure.balance.RegisterProvider;
import com.itsure.balance.ZooKeeperRegistContext;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;

/**
 * @author itsure
 * @date 2019/07/18
 */
public class DefaultRegistProvider implements RegisterProvider {

    @Override
    public void regist(Object context) throws Exception {
        ZooKeeperRegistContext ctx = (ZooKeeperRegistContext) context;
        String path = ctx.getPath();
        ZkClient zkClient = ctx.getZkClient();
        try {
            zkClient.createEphemeral(path, ctx.getData());
        } catch (ZkException e) {
            String parentDir = path.substring(0, path.lastIndexOf('/'));
            zkClient.createPersistent(parentDir, true);
            regist(ctx);
        }

    }

    @Override
    public void unRegist(Object context) throws Exception {
        return;
    }
}
