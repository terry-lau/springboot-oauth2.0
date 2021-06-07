package com.easydatalink.tech.entity;

import java.io.Serializable;
import java.util.Date;

public abstract class IdEntity implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    protected Long id;
    protected String isRemoved = "0";// 是否删除0：使用1：删除
    protected Integer version = 0;
    protected Date createDate;// 创建时间
    protected String createBy;// 创建
    protected Date modifyDate;// 修改时间
    protected String modifyBy;// 修改人


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsRemoved() {
        return isRemoved;
    }

    public void setIsRemoved(String deleted) {
        this.isRemoved = deleted;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

}
