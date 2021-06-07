package com.easydatalink.tech.entity;

import lombok.Data;

/**
 * 响应实体
 * 
 * @author Terry
 * @date 2021/05/06
 */
@Data
public class Result {

    private int code;
    private String message;
    private Object data;

}
