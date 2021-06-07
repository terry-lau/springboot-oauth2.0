package com.easydatalink.tech.service.auth;

import com.easydatalink.tech.entity.auth.CoreUser;

public interface ICoreUserService {

	public CoreUser findUserByLoginName(String longName);
}