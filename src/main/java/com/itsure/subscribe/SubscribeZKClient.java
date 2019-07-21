package com.itsure.subscribe;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author itsure
 * @date 2019/07/18
 */
public class SubscribeZKClient {
    /**
     * 配置节点
     */
    private static final String configPath = "/config";
    /**
     * 服务器注册节点
     */
    private static final String serverPath = "/server";
    /**
     * 命令节点
     */
    private static final String commandPath = "/command";
    /**
     * 启动的服务数量
     */
    private static final int CLIENT_QTY = 10;
    /**
     * zk的服务器地址
     */
    private static final String ZOOKEEPER_SERVER = "101.132.134.183:2181";

    public static void main(String[] args) throws IOException, InterruptedException {
        List<ZkClient> clients = new ArrayList<>();
        List<WorkServer> workServerList = new ArrayList<>();
        ManagerServer managerServer = null;
        try {
            ServerConfig initConfig = new ServerConfig();
            initConfig.setDbUser("root");
            initConfig.setDbPwd("root");
            initConfig.setDbUrl("jdbc:mysql://localhost:3306/mydb");
            ZkClient managerZKClient = new ZkClient(ZOOKEEPER_SERVER, 5000, 5000, new BytesPushThroughSerializer());
            managerServer = new ManagerServer(serverPath, commandPath, configPath, managerZKClient, initConfig);
            managerServer.start();
            for (int i = 0; i < CLIENT_QTY; ++ i) {
                ZkClient wsZKClient = new ZkClient(ZOOKEEPER_SERVER, 5000, 5000, new BytesPushThroughSerializer());
                clients.add(wsZKClient);
                ServerData serverData = new ServerData();
                serverData.setAddress("192.168.1." + i);
                serverData.setId(i);
                serverData.setName("WorkServer#" + i);
                WorkServer workServer = new WorkServer(configPath, serverPath, wsZKClient, serverData, initConfig);
                workServer.start();
                TimeUnit.SECONDS.sleep(1);
            }
            System.out.println("按回车键退出！");
            System.in.read();
        } finally {
            System.out.println("Shutting down...");

            for ( WorkServer workServer : workServerList )
            {
                try {
                    workServer.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for ( ZkClient client : clients )
            {
                try {
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

}
