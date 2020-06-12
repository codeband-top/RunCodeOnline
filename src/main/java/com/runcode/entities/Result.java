package com.runcode.entities;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * HTTP RestApi响应
 * @author RhettPeng
 */
public class Result {
    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回信息
     */
    private Map<String,Object> data = new HashMap<>();

    private Result() {}

    public static Result OK(){
        Result result = new Result();
        result.success = true;
        result.message = "成功！";
        return result;
    }

    public static Result ERROR(){
        Result result = new Result();
        result.success = false;
        result.message = "失败！";
        return result;
    }

    public Result data(String key,Object value){
        this.data.put(key, value);
        return this;
    }

    public Result msg(String msg){
        this.message = msg;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String toJson(){
        return JSONUtil.parse(this).toStringPretty();
    }
}
