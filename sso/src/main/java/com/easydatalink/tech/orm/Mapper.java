package com.easydatalink.tech.orm;

import java.util.List;
import java.util.Map;

import com.easydatalink.tech.entity.IdEntity;

public interface Mapper<T extends IdEntity> {
    
    int deleteByPrimaryKey(Long id);

    int insert(T record);

    int insertSelective(T record);

    T selectByPrimaryKey(Long id);
    List<T> findBatchIds(String[] id);
    /**
     * 分表使用
     * 
     * @param map
     * @return
     */
    T selectByPrimaryKey(Map<String, Object> map);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);

    List<T> getAll();

    void deleteAll();
}