package com.easydatalink.tech.dao.auth.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.easydatalink.tech.dao.auth.ICoreUserDao;
import com.easydatalink.tech.entity.auth.CoreUser;
import com.easydatalink.tech.mapper.auth.CoreUserMapper;
import com.easydatalink.tech.orm.MyBatisDao;

@Repository
public class CoreUserDao extends MyBatisDao<CoreUser> implements ICoreUserDao {

    @Autowired
    private CoreUserMapper coreUserMapper;

    @Override
    public List<CoreUser> betchSelectUserById(List<Object> list) {
        return coreUserMapper.betchSelectUserById(list);
    }

    @Override
    public CoreUser getUserByOpenId(Map<String, Object> values) {
        return coreUserMapper.getUserByOpenId(values);
    }

    @Override
    public CoreUser findUserByLoginName(String userName) {
        return coreUserMapper.findUserByLoginName(userName);
    }

    @Override
    public CoreUser getPlatformNoByName(String accessKeyID) {
        return coreUserMapper.getPlatformNoByName(accessKeyID);
    }

    @Override
    public List<String> getClientInfo(String platfromNo) {
        return coreUserMapper.getClientInfo(platfromNo);
    }
}