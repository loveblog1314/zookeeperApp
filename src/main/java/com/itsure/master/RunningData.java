package com.itsure.master;

import java.io.Serializable;

/**
 * @author itsure
 * @date 2019/07/18
 */
public class RunningData implements Serializable {

    private static final long serialVersionUID = -167329570305124497L;

    /**
     * 服务器id
     */
    private Long cid;
    /**
     * 服务器名称
     */
    private String name;

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
