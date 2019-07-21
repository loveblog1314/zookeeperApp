package com.itsure.subscribe;

import com.alibaba.fastjson.JSON;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

/**
 * @author itsure
 * @date 2019/07/18
 */
public class WorkServer implements IZkDataListener{
    /**
     * config节点路径
     */
    private String configPath;
    /**
     * server节点路径
     */
    private String serverPath;
    /**
     * 开源zk客户端
     */
    private ZkClient zkClient;
    /**
     *当前工作服务器的基本信息
     */
    private ServerData serverData;
    /**
     *服务器初始化配置
     */
    private ServerConfig serverConfig;

    public WorkServer(String configPath, String serverPath, ZkClient zkClient, ServerData serverData, ServerConfig initConfig) {
        this.configPath = configPath;
        this.serverPath = serverPath;
        this.zkClient = zkClient;
        this.serverData = serverData;
        this.serverConfig = initConfig;
    }

    /**
     * 启动workserver服务
     */
    public void start() {
        System.out.println("work server start...");
        initRunning();
    }

    public void stop() {
        System.out.println("work server stop...");
        zkClient.unsubscribeDataChanges(serverPath, this);
    }

    /**
     * 初始化服务
     */
    private void initRunning() {
        registerWS();
        zkClient.subscribeDataChanges(configPath, this);
    }

    /**
     * 向zk注册服务
     */
    private void registerWS() {
        String wsPath = serverPath.concat("/").concat(serverData.getAddress());
        try {
            zkClient.createEphemeral(wsPath, JSON.toJSONString(serverData).getBytes());
        } catch (ZkNoNodeException e) {
            zkClient.createPersistent(serverPath);
            registerWS();
        } catch (ZkException e) {

        }
    }

    private void updateConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public void handleDataChange(String s, Object o) throws Exception {
        String changeServerData = new String((byte[]) o);
        ServerConfig serverConfig = JSON.parseObject(changeServerData, ServerConfig.class);
        updateConfig(serverConfig);
        System.out.println("new work server config is : "+ serverConfig);
    }

    @Override
    public void handleDataDeleted(String s) throws Exception {

    }
}
