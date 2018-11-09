package com.riddler.guide.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.riddler.guide.dto.WsDto;
import com.riddler.guide.util.RedisUtil;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MsgContorller {

    @ResponseBody
    @GetMapping("/stock/kuaixun")
    public WsDto getStockMsg(HttpServletRequest request){
        String page=request.getParameter("page");
        if(null==page||page.isEmpty()) return new WsDto("error","参数page不能为空");
        int num=Integer.parseInt(page);
        if(num<=0) num=0;
        if(num>=100000) num=100000;
        num=num*20;
        List objs=null;
        try(Jedis jedis=RedisUtil.getJedis()){
            List<String> list = jedis.lrange("kuaixun_web_stock", num, num+19);
            objs=list.stream().map(x-> JSON.parseObject(x))
                    .sorted((x,y)->y.getString("time").compareTo(x.getString("time"))).collect(Collectors.toList());
        }
        return new WsDto("success",objs);
    }
 }
