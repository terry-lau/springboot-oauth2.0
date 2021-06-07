package com.easydatalink.tech.service;

import java.util.List;
import java.util.Map;

import com.easydatalink.tech.entity.IdEntity;
import com.easydatalink.tech.orm.Page;

public interface IService<T extends IdEntity> {
    public T get(final Long id);

    public List<T> getAll();

    public void delete(final Long id);

    public void delete(final T entity);

    public void batchDelete(List<Long> ids);

    public void batchDeleteComplete(List<Long> ids);

    public void batchInsert(List<T> entities);

    public void deleteComplete(Long id);

    /**
     * 保存新增的对象.
     */
    public Long insert(T entity);

    /**
     * 判断不为空值就更新
     * 
     * @param entity
     * @return
     */
    public boolean update(T entity);

    /**
     * 不判断直接全部更新
     * 
     * @param entity
     * @return
     */
    public boolean updateAll(T entity);

    public void batchUpdate(List<T> entities);

    public T saveOrUpdate(T entity);

    public Page<T> findPage(final Page<T> page, String sql, final Map<String, ?> values);

}
