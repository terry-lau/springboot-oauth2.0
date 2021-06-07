package com.easydatalink.tech.orm;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.easydatalink.tech.entity.IdEntity;

/**
 * FOR MYBATIS
 * 
 * @author wei.liu
 * @param <T>
 */
@Repository
public interface IMybatisDao<T extends IdEntity> {

    public T get(final Long id);

    public List<T> getAll();

    public List<T> findByIds(List<Long> ids);

    List<T> findBatchIds(String[] ids);

    public List<T> find(final String sql, final Map<String, ?> values);

    public Object findUnique(final String sql, final Map<String, ?> values);

    public void delete(final Long id);

    public void delete(final T entity);

    public void delete(String sql, Map<String, ?> values);

    public void deleteComplete(final Long id);

    /**
     * 保存新增的对象.
     */
    public Long insert(final T entity);

    public Long insertSelective(final T entity);

    public boolean update(final T entity);

    public void batchInsert(List<T> entities);

    public void deleteAll();

    public void batchUpdate(List<T> entities);

    public void batchDelete(List<Long> ids);

    public <X> Page<X> findPage(final Page<X> page, String sql, final Map<String, ?> values);

    public boolean updateAll(T entity);

}
