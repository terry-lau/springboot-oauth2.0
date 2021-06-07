package com.easydatalink.tech.mapper.auth;

import java.util.List;
import java.util.Map;

import com.easydatalink.tech.entity.auth.CoreUser;
import com.easydatalink.tech.orm.Mapper;

public interface CoreUserMapper extends Mapper<CoreUser> {

    public List<CoreUser> betchSelectUserById(List<Object> paramList);

    public CoreUser getUserByOpenId(Map<String, Object> paramMap);

    public CoreUser findUserByLoginName(String paramString);

    CoreUser getPlatformNoByName(String accessKeyID);

    List<String> getClientInfo(String platfromNo);
}
