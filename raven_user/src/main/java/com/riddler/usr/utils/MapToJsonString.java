package com.riddler.usr.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class MapToJsonString extends HashMap<String,Object> {
    public String toJSONString() {
        return JSONObject.toJSONString(this);
    }
    public MapToJsonString add(String key,Object value){
        this.put(key,value);
        return this;
    }
}

