package com.riddler.usr.mapper;

import com.riddler.usr.bean.*;
import com.riddler.usr.utils.PinYinFirstWord;
import com.riddler.usr.utils.StringUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("aliyun")
public class AssetsMapperTest {
    @Autowired
    AssetsMapper assetsMapper;
    @Autowired
    UserMapper userMapper;

    @Test
    public void updateFinstPinYin() throws Exception {
        List<StockIndustry> ss= userMapper.getAllStock();
        int i=0;
        for(StockIndustry s:ss){
            i++;
            String pinyin= PinYinFirstWord.toPinyin(s.getStockname());
            s.setPinyin_firstword(pinyin.toUpperCase());
            userMapper.updateStockIndustryPinYin(s);
            System.out.println(s.getStockname()+"="+i+"="+pinyin);
        }
    }

    @Test
    public void getAppointsss() throws Exception {
        List<StockIndustry> ss= userMapper.getAllStock();
        for(StockIndustry s:ss){
            String code_type= StringUtil.formatStockCode(s.getTicker(),s.getStockname());
            s.setCode(code_type);
            userMapper.updateStockIndustry(s);
        }
    }

    @Test
    public void getAppoint() throws Exception {
        Appointment a=new Appointment();
        a.setPhone_number("1234567");
        userMapper.saveUsrAppointment(a);
    }

    @Test
    public void getCurUserAssetsPoints() throws Exception {
        Assets ass=new Assets();
        ass.setBuy_time("2018-05-29");
        ass.setAssets_code("QQQ");
        Integer point=assetsMapper.getCurUserAssetsPoints("1321a3sf");
        System.out.println(point);

//        System.out.println(assetsMapper.getCurUserAssetsPoints("f5a5804d22335318befd4edcf732f2ee"));
    }

}