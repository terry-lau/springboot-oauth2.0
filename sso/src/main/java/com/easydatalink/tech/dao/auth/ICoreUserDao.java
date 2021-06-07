package com.easydatalink.tech.dao.auth;

import java.util.List;
import java.util.Map;

import com.easydatalink.tech.entity.auth.CoreUser;
import com.easydatalink.tech.orm.IMybatisDao;

public abstract interface ICoreUserDao extends IMybatisDao<CoreUser>
{
  public abstract List<CoreUser> betchSelectUserById(List<Object> paramList);

  public abstract CoreUser getUserByOpenId(Map<String, Object> paramMap);

  public abstract CoreUser findUserByLoginName(String paramString);

	CoreUser getPlatformNoByName(String accessKeyID);

  List<String> getClientInfo(String platfromNo);
}