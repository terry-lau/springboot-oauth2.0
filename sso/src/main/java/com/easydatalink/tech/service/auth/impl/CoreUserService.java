package com.easydatalink.tech.service.auth.impl;

import org.springframework.stereotype.Service;

import com.easydatalink.tech.dao.auth.ICoreUserDao;
import com.easydatalink.tech.entity.auth.CoreUser;
import com.easydatalink.tech.service.MybatisService;
import com.easydatalink.tech.service.auth.ICoreUserService;


@Service
public class CoreUserService extends MybatisService<CoreUser, ICoreUserDao> implements ICoreUserService {

    @Override
    public CoreUser findUserByLoginName(String paramString) {
        return dao.findUserByLoginName(paramString);
    }
}