package com.riddler.guide.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.riddler.guide.bean.SocketMessage;
import com.riddler.guide.common.Constant;
import com.riddler.guide.socket.MyWebSocketHandler;
import com.riddler.guide.util.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


@Service
public class SocketService {

	public List<String> getHistoryNews() {
		try {
			int num = 100;
			Jedis jedis = RedisUtil.getJedis();
			List<String> datas = jedis.lrange("kuaixun_web_stock", 0, num);// kuaixun_web_future
            jedis.close();
			List<String> sortDatas = sortDatas(datas);
			return sortDatas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<String> sortDatas(List<String> datas) {
		if (null != datas && datas.size() > 0) {
			return datas
					.stream()
					.map(x -> JSONObject.parseObject(x))
					.filter(x -> x.containsKey("time"))
					.sorted((x, y) -> y.getString("time").compareTo(
							x.getString("time")))
					.map(x -> JSONObject.toJSONString(x))
					.collect(Collectors.toList());
		}
		return null;
	}

	public void handleUsrMessage(WebSocketSession session,WebSocketMessage<?> message) throws IOException {
		SocketMessage socketMsg = new SocketMessage();
		socketMsg.setType(Constant.USER_MSG_TYPE);
		String msg = (String) message.getPayload();
		System.out.println("<<<<<<<<<<<<<<<<<<"+msg);
		JSONObject obj=null;
		try {
			obj = JSONObject.parseObject(msg);
		} catch (Exception e) {
		}
		if(null!=obj){
			String index_type = getType(obj.getString("type"));
			Jedis jedis = RedisUtil.getJedis();
			List<String> datas = jedis.lrange(index_type, 0, 100);
            jedis.close();
			List<String> sortDatas = sortDatas(datas);
			socketMsg.setData(sortDatas);
			session.sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
			
		}else{
			socketMsg.setData("{content:"+ msg+"}");
			MyWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(socketMsg)));
		}
	}
	
	private String getType(String type) {
		if(StringUtils.isEmpty(type)) return "";
		String index_type = "";
		if ("1".equals(type)) {
			index_type = "kuaixun_web_stock";
		} else if ("2".equals(type)) {
			index_type = "kuaixun_web_future";
		}else{
			index_type = "default_web";
		}
		return index_type;
	}

	public static void main(String[] args) {
		List<String> datas = new SocketService().getHistoryNews();
		for (String s : datas) {
			System.out.println(s);
		}

	}

}
