package com.itsure.subscribe;

/**
 * @author itsure
 * @date 2019/07/18
 */
public class ServerConfig {
    /**
     * 数据库用户名
     */
    private String dbUser;
    /**
     * 数据库地址
     */
    private String dbUrl;
    /**
     * 数据库密码
     */
    private String dbPwd;

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbPwd() {
        return dbPwd;
    }

    public void setDbPwd(String dbPwd) {
        this.dbPwd = dbPwd;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "dbUser='" + dbUser + '\'' +
                ", dbUrl='" + dbUrl + '\'' +
                ", dbPwd='" + dbPwd + '\'' +
                '}';
    }
}
