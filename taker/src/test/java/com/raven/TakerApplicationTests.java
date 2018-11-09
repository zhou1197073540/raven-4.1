package com.raven;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TakerApplicationTests {

    @Test
    public void contextLoads() {
        LocalDate today = LocalDate.now();
        System.out.println(today.getDayOfWeek().toString());
        LocalTime startTime = LocalTime.of(18, 0, 0);
        LocalTime endTime = LocalTime.of(22, 0, 0);
        LocalTime time = LocalTime.now();
        System.out.println(time);
        if (time.isAfter(startTime)) System.out.println("is after 18:00:00");
        else System.out.println("is before 18:00:00");
    }

    @Test
    public void splitContent() throws Exception {
        String content = "000001(平安银行)具有:银行,国际板,信托,余额宝,农村金融,金改等热点.在所属银行行业内比较: PE=12.44,指标上估值高. PB=1.21,指标上估值高. 公司财务情况好. 盈利能力水平差.当前高于分析师目标价:13.67,偏差2.23%.近期资金方面以流入为主 量化模型预测该股下周评级为增持";
//        String content = "000005(世纪星源)具有:国际板,港珠澳大桥,海洋旅游,充电桩,广东自贸区,前海等热点.在所属房地产行业内比较: PE=-209.00,指标上估值高. PB=3.48,指标上估值高. 公司财务情况较好. 盈利能力水平差.无分析师预测.近期资金方面以流出为主 量化模型预测该股下周评级为减持";
        int posHot = content.indexOf("在所属");
        System.out.println("hot : " + content.substring(0, posHot));
        int posIndustry = content.indexOf("公司财务情况");
        System.out.println("industry : " + content.substring(posHot, posIndustry));
        int posFlow = content.indexOf("近期资金方面");
        int posModel = content.indexOf("量化模型预测");
        if (content.contains("无分析师预测")) {
            int posTemp = content.indexOf("无分析师预测");
            System.out.println("basic : " + content.substring(posIndustry, posTemp));
            System.out.println("analysis : 无分析师预测");
        } else {
            int posStart = content.indexOf("盈利能力水平");
            int posAim = content.indexOf("分析师目标价");
            for (int i = posAim; i > posStart; i--) {
                if ('.' == content.charAt(i)) {
                    System.out.println("catch!!");
                    System.out.println("basic : " + content.substring(posIndustry, i));
                    System.out.println("analysis : " + content.substring(i + 1, posFlow));
                    break;
                }
            }
        }
        System.out.println("flow : " + content.substring(posFlow, posModel));
        System.out.println("level : " + content.substring(posModel, content.length()));
    }
}
