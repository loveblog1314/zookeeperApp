package com.itsure.master;

import org.apache.zookeeper.ZooKeeper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author itsure
 * @date 2019/07/18
 */
public class MasterSelectorZkClient {
    /**
     * 启动的服务数量
     */
    private static final int CLIENT_QTY = 10;
    /**
     * zk的服务器地址
     */
    private static final String ZOOKEEPER_SERVER = "101.132.134.183";

    public static void main(String[] args) throws Exception{
        // 保存所有zkClient
        List<ZooKeeper> clients = new ArrayList<>();
        // 保存所有的服务
        List<WorkServer> workServers = new ArrayList<>();

        try {
            for (int i = 0; i < CLIENT_QTY; ++i) {
                // 创建serverData
                RunningData runningData = new RunningData();
                runningData.setCid(Long.valueOf(i));
                runningData.setName("Client #" + i);
                // 创建服务
                WorkServer workServer = new WorkServer(runningData);
                // 创建zkClient
                ZooKeeper client = new ZooKeeper(ZOOKEEPER_SERVER, 5000, workServer);
                clients.add(client);
                workServer.setZooKeeper(client);
                workServers.add(workServer);
                // 开启服务，争抢master
                workServer.start();
                TimeUnit.SECONDS.sleep(1);
            }
            System.out.println("按回车键退出！");
            System.in.read();
        } finally {
            System.out.println("shuting down ...");
            for (WorkServer workServer : workServers) {
                workServer.stop();
            }

            for (ZooKeeper client : clients) {
                client.close();
            }
        }
    }
}
