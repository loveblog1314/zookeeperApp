package com.itsure.balance;

import java.io.Serializable;

/**
 * @author itsure
 * @date 2019/07/18
 */
public class ServerData implements Serializable, Comparable<ServerData> {

    private static final long serialVersionUID = -885243084212858431L;
    /**
     * 服务器负载
     */
    private Integer balance;
    /**
     * 服务器ip
     */
    private String host;
    /**
     * 服务器端口
     */
    private Integer port;

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "ServerData{" +
                "balance=" + balance +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public int compareTo(ServerData o) {
        return this.getBalance().compareTo(o.getBalance());
    }
}
