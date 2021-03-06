package com.riddler.guide.util;

import com.alibaba.fastjson.JSON;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class RedisUtilTest {

    public static void main(String[] args) {
        List<String> list=new ArrayList<>();
        list.add("{\"time\":\"2018-05-02 16:14:00\",\"title\":\"北京丰台新村街道“吹哨”“报到”破解管理顽症\",\"content\":\"\\n　　近期，北京市丰台区新村街道开展环境整治百日会战，对丰南路建筑垃圾进行清理。丰台区新村街道供图\\n千龙网发　　千龙网北京5月2日讯记者5月2日获悉，北京市丰台区新村街道4月起在辖区范围内开展环境建设百日会战，通过“街乡吹哨、部门报到”机制，运用城市管理综合执法平台，成功解决了一些辖区群众诉求和历史遗留问题，办成了一些急事大事，成效显著。　　新村街道启动环境整治百日会战，主要涉及市容卫生、环境秩序、交通秩序、无证无照经营及“开墙打洞”、违法建设、地下空间、群租房等七个方面的专项整治。为解决突出疑难问题，采取社区吹哨，街道执法报到，运用非现场执法、联合执法和案件移送等执法措施，进行重点解决，共同进行整治。　　据悉，为助推“街乡吹哨、部门报到”机制有效落地，新村街道在综合执法中心平台成立之时，就建立街道和社区二级体系。在社区一级，由志愿者巡查发现问题上报，社区协调解决不了的问题，再上报至街道“吹哨报到”体系中，公安、城管、工商、交通、食药监管等部门报到，解决问题，反馈结果。街道解决不了的问题，上报区级部门解决。　　新村街道综合执法中心成立以来，1个多月累计“吹哨”“报到”28次，涉及环境、环保、民生和社会治理方面。新村街道富丰园社区党委书记曹海青表示，通过综合执法、部门联动，整合了执法资源，明晰了权力责任，执法力度更大了，积极性更高了，效果更好了。阻碍执法的也变的少了，混乱无序的“场面”基本看不见了。　　“平台成立后，执法效果更加理想。社区志愿者在巡查中，发现一个非法喷漆车间，立即向社区报告，社区马上吹哨，不到20多分钟，街道综合执法中心的工商、食药、城管、公安等人员来到现场，联合环保部门取缔并关停刘庄子路119号非法喷漆车间。”新村街道丰西社区党委书记王兴旺介绍。　　新村街道综合执法中心负责人介绍，“街乡吹哨、部门报到”真正在新村落地生根、形成了生动实践。截止目前，街道已拆除违建3100余平米，整治群租房52处，腾退地下室1个23间；查处无照经营、游商摊贩256起，处罚65起、1.3万元，暂扣物品70件，清理“僵尸车”等，让街区环境、秩序面貌焕然一新。下一步，街道将继续深化“街乡吹哨、部门报到”机制，坚持最高标准，促进辖区经济建设、社会稳定和谐。\\n（责任编辑：\\nHN666）\\n看全文\\n想了解更多关于《北京丰台新村街道\\\"吹哨\\\"\\\"报到\\\"破解管理顽症》的报道，那就扫码下载和讯财经APP阅读吧。\\n\",\"index_type\":\"stock\"}");
        list.add("{\"time\":\"2018-05-02 16:22:32\",\"title\":\"美国经济正走向衰退？关注这四大警告信号\",\"content\":\"\\n　　随着美国接近有史以来最长的经济扩张，人们不禁要问：美国何时将陷入衰退，我们如何知道？\\n　　据圣路易斯联储，美国经济陷入衰退前通常会发生这些：几乎在战后每一次经济衰退之前，油价都飙升；最近两次经济衰退前，资产泡沫膨胀；自1960年以来的所有衰退之前，收益率曲线出现倒挂。\\n　　美国财经网(博客,微博)站Marketwatch将所有这些归入四个图表，包括油价、收益率曲线、股市的简单估值工具及房地产。从中可以看出美国经济似乎已出现危机信号。\\n　　油价\\n　　目前，因OPEC实施减产协议，油价处于上升趋势。下图给出了三个时期的油价，分别是美国互联网泡沫时期、美国大衰退（2006年到2010年金融危机）时期以及过去5年的石油价格。从图中可以看出，2008年金融危机爆发前夕，油价曾一度触及130美元/桶的高位，此后油价回落，一度跌至30美元下方。而近期油价再次反弹，曾一度触及三年半新高。\\n收益率曲线\\n　　收益率曲线是指10年期美国国债收益率减去2年期美国国债收益率。好消息是，尽管收益率曲线显著趋平，但尚未出现倒挂（也就是两年期美债收益率高于10年期美债收益率）。\\n　　下图给出了美国互联网泡沫时期、美国大衰退时期以及过去5年的收益率曲线：\\n　　为何收益率曲线倒挂是经济衰退的先行指标仍是存在争议，旧金山联储在3月的报告中给出了这样的解释：\\\"长期利率反映了市场对未来经济状况的预期。尽管在经济扩张的早期，长期利率随着短期利率上升，一旦投资者对经济前景变得越来越悲观，长期利率倾向于停止上升。收益率曲线趋平也使银行短期借款和长期贷款的利润下降，这可能会抑制贷款供给，并收紧信贷状况。尽管如此，利率与宏观经济之间的复杂关系，导致很难确定收益率曲线倒挂与经济放缓之间联系的确切机制。\\\"\\n　　标普500指数录得十年最长修正时间\\n　　标普500指数从2月8日进入修正区间，现在已经过去57个交易日，为2008年5月1日以来最长修正时间。\\n　　2月8日，标普500指数和道琼斯指数一起跌入回调区间。此后，标普500指数就一直处于修正区间，因其从未突破1月26日创下的纪录高位。\\n　　截至5月1日，标普500指数从近期高点下跌了约8.5%，而道指从1月底的纪录高点下跌了10.4%，纳斯达克指数从近期创纪录高点下跌7.7%。\\n　　据《华尔街日报(博客,微博)》的数据，自1950年以来，美股平均修正时间为61个交易日，而过去五次的修正时间仅为37个交易日。\\n　　下图使用的是托宾的Q比率(Tobin&quot;s\\nQ\\nRatio)，是公司的市场价值/资产重置成本，由诺贝尔经济学奖得主詹姆斯・托宾（James\\nTobin）于1969年提出。Tobin&quot;s\\nQ可以反映市场对于公司未来利润的预期，并对公司投资产生影响。\\n根据这一原理，当股票价格上扬时，\\nTobin&quot;s\\nQ值会随之增加，企业将会更多的在资本市场上发行股票来进行融资，投资新设备也将会增加，从而带来产出的增加。\\n　　从下图看出，股市似乎并没有像互联网泡沫期间那样被高估。\\n房价\\n　　下图为房价与房租的比率。与次贷危机期间相比，美国房地产市场似乎并没有估值过高。\\n（责任编辑：何美铃\\nHF117）\\n看全文\\n想了解更多关于《美国经济正走向衰退？关注这四大警告信号》的报道，那就扫码下载和讯财经APP阅读吧。\\n\",\"index_type\":\"stock\"}");
        list.add("{\"time\":\"2018-05-02 16:20:00\",\"title\":\"港股收评：恒指震荡收跌0.27% 内银内险股集体下跌\",\"content\":\"\\n　　中证网讯\\n（记者\\n田鸿伟）\\n5月2日消息，港股恒生指数今日低开0.09%，盘中一度翻红，随后跌幅扩大，在30630点上下波动。截至收盘，恒指跌0.27%，报30723.88点。国企指数跌1.12%，报12193.59点。红筹指数跌0.3%，报4527.23点。大市成交1043.33亿港元。　　内银股下跌。交通银行跌2.32%，领跌蓝筹，招商银行跌2.31%，建设银行跌1.08%。　　内险股全面下挫。截至收盘，中国平安跌1.1%，中国太保(601601,股吧)跌1.72%，中国太平跌0.94%，中国人寿跌0.89%，新华保险(601336,股吧)跌2.03%。　　苹果公布的2018财年第二财季财报显示，营收和净利润胜预期，均创史上单季最高记录。受此影响，苹果概念股集体上涨，舜宇光学涨4.92%，高伟电子涨6.59%，通达集团涨4%。　　小米于今日递交上市申请，最快在今年6月底至7月初挂牌，成为港交所首批\\\"同股不同权\\\"上市公司。同时，小米预计集资至少100亿美元，成为港股今年募资最大的新股。　　沪深港通下的每日额度，由今日起扩大至原来的4倍。截至今日收盘，沪股通净流入23.91亿元，当日余额为496.09亿元；深股通净流入23.38亿元，当日余额为496.62亿元，北向资金合计净流入47.29亿元。早盘期间随着两市股指冲高，沪股通和深股通均呈现加速流入状态，午后流入速度趋缓。　　分析认为，沪深港通扩容是为了A股下月起纳入MSCI新兴市场指数作准备，反映内地进一步开放资本市场。预计A股\\\"入摩\\\"后，初期流入的资金100多亿美元，规模不大，加上沪深港通每日额度很少用完，短期对港股影响有限。　　对于港股后市，分析人士表示，当前外围美股疲弱，加上美联储加息的担忧，令港股承压。另外，5月A股\\\"入摩\\\"和港股公司业绩释放，也可能会给港股带来正面支持，不过如果新增资金流入谨慎，港股反弹空间不大。因此，预计港股近期走势先抑后扬。\\n（责任编辑：\\nHN666）\\n看全文\\n想了解更多关于《港股收评：恒指震荡收跌0.27%\\n内银内险股集体下跌》的报道，那就扫码下载和讯财经APP阅读吧。\\n\",\"index_type\":\"stock\"}");
        List objs=list.stream().map(x->JSON.parseObject(x))
                .sorted((x,y)->y.getString("time").compareTo(x.getString("time"))).collect(Collectors.toList());
        for(Object str:objs){
            System.out.println(str);
        }
//        for(String str:list){
//            System.out.println(str);
//        }
    }
}