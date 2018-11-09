package com.raven.fake;

import com.google.common.collect.ImmutableMap;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Controller
public class FakeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FakeApplication.class, args);
    }


    @RequestMapping(value = "/user_stock_info")
    @ResponseBody
    public Map<String, Object> fakeUserStockInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return ImmutableMap.of("error", "token非法");
        }
        RespDto dto = new RespDto();
        Map<String,Object> main = new HashMap<>();
        Map<String, Object> info = new HashMap<>();
        info.put("total_assets", "58000.88");
        info.put("shares_value", "999999999.88");
        info.put("total_pl", "999999999.88");
        info.put("annualized_returns", "0.0052");
        List<Map<String, Object>> current_stocks = new ArrayList<>();
        Map<String, Object> m = null;
        for (int i = 0; i < 15; i++) {
            m = new HashMap<>();
            m.put("code", "999999");
            m.put("name", "万智慧投");
            m.put("cost_price", "0.01");
            m.put("market_price", "9999.99");
            m.put("amount", "99999");
            current_stocks.add(m);
        }
        List<Map<String, Object>> trans_details = new ArrayList<>();
        Map<String, Object> m1 = null;
        for (int i = 0; i < 15; i++) {
            m1 = new HashMap<>();
            m1.put("time", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            m1.put("code", "999999");
            m1.put("name", "万智慧投");
            m1.put("bs_type", "B");
            m1.put("volume", "999999");
            m1.put("price", "100000");
            trans_details.add(m1);
        }
        info.put("current_stocks", current_stocks);
        info.put("trans_details", trans_details);
        return info;
    }
}
