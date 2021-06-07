##授权clientId
security.oauth2.client.client-id=sys
##授权secret
security.oauth2.client.client-secret=user123
##授权方式《密码》（带上账号和密码）去获取token
##http://127.0.0.1:8086/sso/oauth/token?grant_type=password&username=admin&password=123456
##刷新token
##http://127.0.0.1:8086/sso/oauth/token?grant_type=refresh_token&client_id=sys&client_secret=user123&refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MjI5NjgwNzIsInVzZXJfbmFtZSI6ImFkbWluIiwianRpIjoiYjJkNTg3OGYtMDYxOS00YzE1LTg1MzQtNjNkNWMyNTBjNjE0IiwiY2xpZW50X2lkIjoiZGVjbGFyZSIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdLCJhdGkiOiI1MzA3NjUwZi02NTIyLTQ5MDItYTZhNS1mNTc4Njk0ZDAyMzIifQ.l_Ln3HaSWkX5t7qzuUgwSBTplX3qW1iGekLF0EQiACU
security.oauth2.client.access-token-uri=http://127.0.0.1:8086/sso/oauth/token
##通过认证服务器拦截登陆成功返回-系统地址?code=授权code
##http://localhost:8086/sso/oauth/authorize?response_type=code&client_id=sys&redirect_uri=系统地址
security.oauth2.client.user-authorization-uri=http://127.0.0.1:8086/sso/oauth/authorize
##通过授权code去获取token
##http://127.0.0.1:8086/sso/oauth/token?code=owu1lz&grant_type=authorization_code&redirect_uri=系统地址
##checkToken 合法性
##http://127.0.0.1:8086/sso/oauth/check_token?token=token
security.oauth2.client.user-authorization-check-uri=http://127.0.0.1:8086/sso/oauth/check_token

/oauth/authorize：授权端点
/oauth/token：令牌端点
/oauth/confirm_access：用户确认授权提交端点
/oauth/error：授权服务错误信息端点
/oauth/check_token：用于资源服务访问的令牌解析端点
/oauth/token_key：提供公有密匙的端点，如果你使用JWT 令牌的话
