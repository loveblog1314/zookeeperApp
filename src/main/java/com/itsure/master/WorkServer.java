package com.itsure.master;

import com.alibaba.fastjson.JSON;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author itsure
 * @date 2019/07/18
 */
public class WorkServer implements Watcher{
    /**
     * 记录当前服务器状态
     */
    private volatile  boolean running = false;
    /**
     * 操作zk的开源客户端
     */
    private ZooKeeper zooKeeper;
    /**
     * master的zk节点路径
     */
    private static final String MASTER_PATH = "/master";
    /**
     * 当前服务器节点信息
     */
    private RunningData serverData;
    /**
     * master服务器节点信息
     */
    private RunningData masterData;

    /**
     * 线程调度器
     */
    private ScheduledExecutorService delayExecutor = Executors.newScheduledThreadPool(1);
    /**
     * 延迟时间
     */
    private  int delayTime = 5;

    public WorkServer(RunningData rd) {
        this.serverData = rd;
    }

    public WorkServer() {
    }

    /**
     * 开启服务
     * @throws Exception
     */
    public void start() throws Exception {
        if (running) {
            throw  new Exception("server has startup...");
        }
        running = true;
        takeMaster();
    }

    /**
     * 停止服务
     * @throws Exception
     */
    public void stop() throws Exception {
        if (!running) {
            throw new Exception("server has stoped");
        }
        running = false;
        releaseMaster();
    }

    /**
     * 增强master权力
     * @throws Exception
     */
    public void takeMaster() {
        if (!running) return;
        try {
            String workServerSerial = JSON.toJSONString(serverData);
            zooKeeper.create(MASTER_PATH, workServerSerial.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            masterData = serverData;
            System.out.println("当前master:"+masterData.getName());
            // 模拟网络抖动
            delayExecutor.schedule(new Runnable() {
                @Override
                public void run() {
                    if (checkMaster()){
                        try {
                            releaseMaster();
                        } catch (Exception e) {
                        }
                    }
                }
            }, delayTime, TimeUnit.SECONDS);
        } catch (KeeperException.NodeExistsException e) {
            Stat stat = new Stat();
            byte[] nodeData = new byte[0];
            try {
                nodeData = zooKeeper.getData(MASTER_PATH, true, stat);
            } catch (KeeperException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            RunningData runningData = JSON.parseObject(nodeData, RunningData.class);
            if (runningData == null) {
                takeMaster();
            } else {
                masterData = runningData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放master权力
     * @throws Exception
     */
    public void releaseMaster() throws Exception {
        if (checkMaster()) {
            System.out.println("主动释放master" + masterData.getName());
            zooKeeper.delete(MASTER_PATH, -1);
        }
    }

    /**
     * 检查是不是master节点
     * @return
     */
    public boolean checkMaster() {
        try {
            Stat stat = new Stat();
            byte[] eventData = zooKeeper.getData(MASTER_PATH, this, stat);
            RunningData currentData = JSON.parseObject(eventData, RunningData.class);
            masterData = currentData;
            if (masterData.getName().equals(serverData.getName())) {
                return true;
            }
        } catch (KeeperException e) {
            return checkMaster();
        } catch (InterruptedException e) {
            return false;
        }
        return false;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case NodeCreated:
                break;
            case NodeDeleted:
                System.out.println("节点被删除，master断开连接，重新选举");
                // 网络抖动解决方案
                if (masterData != null && masterData.getName().equals(serverData.getName())) {
                    takeMaster();
                } else {
                    delayExecutor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            takeMaster();
                        }
                    }, delayTime, TimeUnit.SECONDS);
                }
                break;
            case NodeDataChanged:
                break;

        }
    }
}
