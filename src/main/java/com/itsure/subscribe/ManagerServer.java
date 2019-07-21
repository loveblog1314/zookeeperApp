package com.itsure.subscribe;

import com.alibaba.fastjson.JSON;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import java.util.List;

/**
 * @author itsure
 * @date 2019/07/18
 */
public class ManagerServer implements IZkDataListener, IZkChildListener {
    /**
     * 监听server list的变化，所以用到了server节点
     */
    private String serverPath;
    /**
     * 监听command节点
     */
    private String commandPath;
    /**
     * 监听配置节点
     */
    private String configPath;

    private ZkClient zkClient;
    /**
     * 服务器默认配置
     */
    private ServerConfig serverConfig;
    /**
     * 服务器列表
     */
    private List<String> workServerList;

    public ManagerServer(String serverPath, String commandPath, String configPath, ZkClient zkClient, ServerConfig serverConfig) {
        this.serverPath = serverPath;
        this.commandPath = commandPath;
        this.configPath = configPath;
        this.zkClient = zkClient;
        this.serverConfig = serverConfig;
    }

    public void start() {
        initRunning();
    }

    public void stop() {
        zkClient.unsubscribeDataChanges(commandPath, this);
        zkClient.unsubscribeChildChanges(serverPath, this);

    }

    private void initRunning() {
        zkClient.subscribeDataChanges(commandPath, this);
        zkClient.subscribeChildChanges(serverPath, this);
    }

    private void execCmd(String cmd) {
        if ("list".equals(cmd)) {
            execList();
        } else if ("create".equals(cmd)) {
            execCreate();
        } else if ("modify".equals(cmd)) {
            execModify();
        } else {
            System.err.println("没有该命令");
        }
    }

    private void execList() {
        System.out.println(workServerList.toString());
    }

    private void execCreate() {
        if (!zkClient.exists(configPath)) {
            try {
                zkClient.createPersistent(configPath, JSON.toJSONString(serverConfig).getBytes());
            } catch (ZkNodeExistsException e) {
                zkClient.writeData(configPath, JSON.toJSONString(serverConfig).getBytes());
            } catch (ZkException e) {
                System.err.println("遇到了多个配置");
            }
        }
    }

    private void execModify() {
        serverConfig.setDbUser(serverConfig.getDbUser() + "_modify");
        try {
            zkClient.writeData(configPath, JSON.toJSONString(serverConfig).getBytes());
        } catch (ZkNoNodeException e) {
           execCreate();
        }
    }


    @Override
    public void handleDataChange(String s, Object o) throws Exception {
        String cmd = new String((byte[]) o);
        System.out.println("收到命令cmd :" + cmd);
        execCmd(cmd);
    }

    @Override
    public void handleDataDeleted(String s) throws Exception {

    }

    @Override
    public void handleChildChange(String s, List<String> list) throws Exception {
        this.workServerList = list;
        System.out.println("work server list is changed ,new list is :");
        execList();
    }
}
