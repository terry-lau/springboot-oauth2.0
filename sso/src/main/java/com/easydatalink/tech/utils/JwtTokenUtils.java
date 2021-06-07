package com.easydatalink.tech.utils;

import java.util.Calendar;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * JWT 工具类
 * 
 * @author Terry
 * @date 2021/05/07
 */
public class JwtTokenUtils {

    // SING签名的设置，不能对外暴露
    public static final String PRIVATE_KEY = "szYh2021!J1JK3JH^&g%f*f@f*(f!)fs*#s*$H3J4DK43";

    /**
     * @Description 生成token header.payload.sing
     * @Param Map 集合存入payload信息
     * @return
     */
    public static String getToken(Map<String, String> map) {
        // 设置令牌的过期时间
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 7);

        // 创建JWT builder
        JWTCreator.Builder builder = JWT.create();
        // payload
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });
        String token = builder.withExpiresAt(instance.getTime()) // 指定令牌过期时间
            .sign(Algorithm.HMAC256(PRIVATE_KEY));
        return token;
    }

    /**
     * 验证令牌是否合法
     * 
     * @param token
     */
    public static void verify(String token) {
        JWT.require(Algorithm.HMAC256(PRIVATE_KEY)).build().verify(token);
    }

    /**
     * 获取JWT的信息
     * 
     * @param token
     * @return
     */
    public static DecodedJWT getTokenInfo(String token) {
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(PRIVATE_KEY)).build().verify(token);
        return verify;
    }

    public static void main(String[] args) {
        JWTVerifier build = JWT.require(Algorithm.HMAC256(PRIVATE_KEY)).build();
        DecodedJWT verify = build.verify(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MjE4MjYzNzcsInVzZXJfbmFtZSI6ImFkbWluIiwianRpIjoiZmYwMTMyYTktMGJkYS00ZTEwLTg0ZDgtYzNhZDEzM2YyMTYxIiwiY2xpZW50X2lkIjoiZGVjbGFyZW1hbmFnZSIsInNjb3BlIjpbImFsbCJdfQ.5elVJJgR3ImxXFANmeFfMwr3bW0u0U-h2dEd5JQH9Sw");
        System.out.println(verify.getClaim("user_name").asString());// 获取添加的数据->进行取出
    }

}
