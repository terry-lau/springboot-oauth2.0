package com.easydatalink.tech.dao;

import java.util.List;
import java.util.Map;

import com.easydatalink.tech.entity.IdEntity;
import com.easydatalink.tech.orm.Page;

public interface IDao<T extends IdEntity> {

    public T get(final Long id);

    public List<T> getAll();

    public T findUniqueBy(String propName, Object propValue);

    public List<T> findByIds(List<Long> ids);

    public List<T> find(final String sql, final Map<String, ?> values);

    public T findUnique(final String sql, final Map<String, ?> values);

    public void delete(final Long id);

    public void delete(final T entity);

    public int delete(String sql, Map<String, ?> values);

    public void deleteComplete(final Long id);

    /**
     * 保存新增的对象.
     */
    public Long insert(final T entity);

    public boolean update(final T entity);

    public List<T> batchInsert(List<T> entities);

    public void batchUpdate(List<T> entities);

    public void batchDelete(List<Long> ids);

    public <X> Page<X> findPage(final Page<X> page, String sql, final Map<String, ?> values);

}