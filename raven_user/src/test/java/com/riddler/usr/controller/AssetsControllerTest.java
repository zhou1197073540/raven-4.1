package com.riddler.usr.controller;

import com.riddler.usr.bean.Assets;
import com.riddler.usr.bean.AssetsCost;
import com.riddler.usr.service.AsstesService;
import com.riddler.usr.utils.TimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AssetsControllerTest {
    @Autowired
    AsstesService assetsService;

    @Test
    public void calculateETFCost() throws Exception {
        String point="500",uid="955ab77f26c6aafc0524b01e3b76bebc",oid="67c2eaa3a2fc637307417a995f09aad9";
        if(point.isEmpty()||uid==null||uid.isEmpty())return ;
        String cur_date= TimeUtil.getDate(-1);//当前的日期是用昨天的，因为今天的etf股票没有出现，所以用昨天的
        String cur_points=assetsService.getCurUserAssetsPoints(uid);//用户当前资产积分
        List<Assets> assets = assetsService.getNewestAssets();
        for(Assets asset:assets){
            if(cur_points==null){
//                AssetsCost cos=new AssetsCost();
//                cos.setCode(asset.getAssets_code());
//                cos.setCost_price(asset.getLastest_price()+"");
//                cos.setDate(cur_date);
//                cos.setUid(uid);
//                assetsService.saveOrUpdateCost(cos);
            }else{
                assetsService.calETFCost(oid,uid,cur_points,point,asset);
            }
        }
    }

}