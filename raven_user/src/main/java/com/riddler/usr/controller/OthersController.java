package com.riddler.usr.controller;

import com.alibaba.fastjson.JSONObject;
import com.riddler.usr.annotation.AuthCheck;
import com.riddler.usr.bean.NewsVO;
import com.riddler.usr.bean.StockIndustry;
import com.riddler.usr.bean.Ticker;
import com.riddler.usr.bean.TrickerAnalysis;
import com.riddler.usr.common.Auth;
import com.riddler.usr.common.MsgConstant;
import com.riddler.usr.dto.ResponseBaseDTO;
import com.riddler.usr.service.OthersService;
import com.riddler.usr.service.UserService;
import com.riddler.usr.utils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.riddler.usr.common.Const.V_CODE_EX_TIME;

@RestController
public class OthersController {
    public static final Logger logger = LoggerFactory.getLogger(AccessTokenUtil.class);
    @Autowired
    TokenUtil tokenUtil;
    @Autowired
    UserService userService;
    @Autowired
    OthersService othersService;

    /**
     * 生成--图片验证码
     */
    @GetMapping("/authCode")
    public void generageImageCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //表明生成的响应是图片
        response.setContentType("image/jpeg");

        BufferedImage image = VerificationCodeUtil.createImage(request);
        ImageIO.write(image, "JPEG", response.getOutputStream());
    }

    /**
     * 验证客户端发送过来的--手机验证码
     */
    @ResponseBody
    @PostMapping("/checkCode")
    public ResponseBaseDTO CheckCode(HttpServletRequest request, @RequestBody JSONObject json) throws Exception {
        // 获取存放在session中的验证码
        String code = (String) request.getSession().getAttribute("code");
        // 获取页面提交的验证码
        String inputCode = json.getString("code");
        String phone_num = json.getString("phone_num");
        if (null == code || null == inputCode || null == phone_num)
            return new ResponseBaseDTO(MsgConstant.ERROR, "电话号码或验证码不能为空");
        //验证电话号码是否存在
        if (!userService.checkPhoneIsExist(phone_num)) return new ResponseBaseDTO(MsgConstant.ERROR, "电话号码不存在");
        if (code.toLowerCase().equals(inputCode.toLowerCase())) { // 验证码不区分大小写
            // 验证成功，跳转到成功页面
            return doSendMsg(phone_num);
        } else { // 验证失败
            return new ResponseBaseDTO(MsgConstant.ERROR, "验证码错误");
        }
    }

    /**
     * 给手机发送验证码
     */

    public ResponseBaseDTO doSendMsg(String phoneNum) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String randomNum = HashUtil.generateRandomNum(6);
        System.out.println("randomNum : " + randomNum);
        Jedis jedis = RedisUtil.getJedis();
        jedis.setex(phoneNum + "_vcode", V_CODE_EX_TIME, randomNum);
        jedis.close();
        int ret = SmsUtil.singleSend(randomNum, phoneNum);
        if (ret == 0) {
            responseBaseDTO.setStatus(MsgConstant.SUCCESS);
            responseBaseDTO.setData("send_already");
        } else if (ret == 33) {
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setData("30s allow 1 msg vcode");
        } else if (ret == 22) {
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setData("1h allow 3 msg vcode");
        }
        return responseBaseDTO;
    }

    /**
     * 生成公众号二维码
     */
    @GetMapping("/wechat_QR_code")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO generageWechat_QR_code(@RequestHeader("Authorization") String token) throws Exception {
        String uid = tokenUtil.getProperty(token, "uid");
//        String uid="f5a5804d22335318befd4edcf732f2ee";
        int id = userService.findUserIdByUid(uid);

        //生成二维码所需要的ticket
        String QR_ticket = WechatUtil.generateQRTicket(id);
        if (StringUtils.isBlank(QR_ticket)) {
            return new ResponseBaseDTO(MsgConstant.ERROR, "QR_ticket 为空");
        }
        logger.info("用户id:{},QR_ticket:{}", id, QR_ticket);
        String QR_code = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s";
        return new ResponseBaseDTO(MsgConstant.SUCCESS, String.format(QR_code, QR_ticket));
    }

    /**
     * 检查用户是否订阅微信服务号
     */
    @GetMapping("/checkSubscribe")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO checkSubscribe(@RequestHeader("Authorization") String token) throws Exception {
        String uid = tokenUtil.getProperty(token, "uid");
//        String uid="f5a5804d22335318befd4edcf732f2ee";
        int id = userService.findUserIdByUid(uid);
        if (id > 0) {
            if (userService.findIdByWXInfo(id)) {
                return new ResponseBaseDTO(MsgConstant.SUCCESS, "已订阅");
            } else {
                String QR_ticket = WechatUtil.generateQRTicket(id);
                if (StringUtils.isBlank(QR_ticket)) {
                    return new ResponseBaseDTO(MsgConstant.ERROR, "QR_ticket 为空");
                }
                logger.info("用户id:{},QR_ticket:{}", id, QR_ticket);
                String QR_code = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s";
                return new ResponseBaseDTO(MsgConstant.SUCCESS, String.format(QR_code, QR_ticket));
            }
        }
        return new ResponseBaseDTO(MsgConstant.ERROR, "系统错误");
    }

    /**
     * 股价异动排行榜（个股分析页面）
     */
    @GetMapping("/stock_rate_ranking")
    @ResponseBody
    public ResponseBaseDTO stockRateRanking() {
        //取上涨10条数据
        List<Ticker> upTickers = othersService.selectRiseStocks();
        if (upTickers.size() <= 0) return new ResponseBaseDTO(MsgConstant.ERROR, "数据为空");
        int num = 1;
        List<Ticker> removeSameUpTickers = new ArrayList<>();
        Map<String, String> upMap = new HashMap<>();
        for (Ticker upTicker : upTickers) {
            if (upMap.containsKey(upTicker.getTicker())) continue;
            upMap.put(upTicker.getTicker(), null);
            upTicker.setRank(num);
            removeSameUpTickers.add(upTicker);
            if (num > 9) break;
            num++;
        }
        //取下跌10条数据
        List<Ticker> fallTickers = othersService.selectFallStocks();
        if (fallTickers.size() <= 0) return new ResponseBaseDTO(MsgConstant.ERROR, "数据为空");
        int numm = 1;
        List<Ticker> removeSameFallTickers = new ArrayList<>();
        Map<String, String> fallMap = new HashMap<>();
        for (Ticker fallTicker : fallTickers) {
            if (fallMap.containsKey(fallTicker.getTicker())) continue;
            fallMap.put(fallTicker.getTicker(), null);
            fallTicker.setRank(numm);
            removeSameFallTickers.add(fallTicker);
            if (numm > 9) break;
            numm++;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("rise", removeSameUpTickers);
        map.put("fall", removeSameFallTickers);
        return new ResponseBaseDTO(MsgConstant.SUCCESS, map);
    }

    /**
     * 领涨主题（个股分析页面）
     */
    @GetMapping("/leading_theme")
    @ResponseBody
    public ResponseBaseDTO leadingTheme() {
        List<String> stocks = othersService.selectLeadingTheme();
        if (stocks.size() > 0) return new ResponseBaseDTO(MsgConstant.SUCCESS, stocks);
        return new ResponseBaseDTO(MsgConstant.ERROR, "数据为空");
    }

    /**
     * 个股公告（个股分析页面）
     */
    @PostMapping("/ticker_notice")
    @ResponseBody
    public ResponseBaseDTO tickerNotice(@RequestBody JSONObject objecet) {
        String tickerCode = objecet.getString("tickerCode");
        List<TrickerAnalysis> trickers = othersService.selectTickerNotice(tickerCode);
        return new ResponseBaseDTO(MsgConstant.SUCCESS, trickers);
    }

    /**
     * 个股新闻（个股分析页面)
     */
    @PostMapping("/ticker_news")
    @ResponseBody
    public ResponseBaseDTO tickerNews(@RequestBody JSONObject objecet) {
        String tickerCode = objecet.getString("tickerCode");
        List<TrickerAnalysis> trickers = othersService.selectTickerNews(tickerCode);
        return new ResponseBaseDTO(MsgConstant.SUCCESS, trickers);
    }


    @RequestMapping(value = "/diag/{code}")
    @ResponseBody
    public ResponseBaseDTO diagTicker(@PathVariable String code) {
        if (StringUtils.isBlank(code)) {
            return new ResponseBaseDTO(MsgConstant.ERROR, "code为空，请检查");
        }
        Map<String, String> content = null;
        try {
            Pattern p = Pattern.compile("^\\d+$");
            Matcher m = p.matcher(code);
            if (m.find()) {
//				英文股票代码
                content = othersService.getJsonStr(code);
                if (null == content) {
                    content = othersService.select2sw_stock_industry(code);
                }
            } else {
                //中文股票
                String codeNum = othersService.selectCodeNum(code);
                if (!StringUtils.isBlank(codeNum)) {
                    content = othersService.getJsonStr(codeNum);
                    if (null == content) {
                        content = othersService.select2sw_stock_industry(codeNum);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == content) {
            return new ResponseBaseDTO(MsgConstant.ERROR, "查询结果不存在，请检查code是否正确");
        } else {
            return new ResponseBaseDTO(MsgConstant.SUCCESS, content);
        }
    }

    @RequestMapping(value = "/findStock/{code}")
    @ResponseBody
    public ResponseBaseDTO diagList(@PathVariable String code) {
        if (!code.isEmpty()) {
            List<StockIndustry> stocks = othersService.findStockNotEnd(code);
            return new ResponseBaseDTO("success", stocks);
        }
        return new ResponseBaseDTO("error", "参数有误");
    }

    /**
     * 查询股票接口
     * @param request
     * @return
     */
    @GetMapping(value = "/findStock")
    @ResponseBody
    public ResponseBaseDTO diagStock(HttpServletRequest request) {
        String code=request.getParameter("code");
        if (!code.isEmpty()) {
            List<StockIndustry> stocks = othersService.findStockNotEnd(code.toUpperCase());
//            for(StockIndustry stock:stocks){
//                String reCode=StringUtil.formatStockCode(stock.getTicker(),stock.getStockname() );
//                stock.setCode(reCode);
//            }
            return new ResponseBaseDTO("success", stocks);
        }
        return new ResponseBaseDTO("error", "参数有误");
    }
    /**
     * 个股公告（个股分析页面）
     */
    @GetMapping("/ticker_notice")
    @ResponseBody
    public ResponseBaseDTO tickerNotice(HttpServletRequest request) {
        String tickerCode = StringUtil.rmStockCode(request.getParameter("tickerCode"));
        if (StringUtil.isEmpty(tickerCode)) return new ResponseBaseDTO("error", "参数为空或者该股票不存在");
        List<TrickerAnalysis> trickers = othersService.selectTickerNotice(tickerCode);
        for (TrickerAnalysis ticker : trickers) {
            String content = ticker.getContent();
            if (content.length() > 100) {
                String shortContent = ticker.getContent().substring(0, 100);
                ticker.setContent(shortContent + "...");
            } else {
                ticker.setContent(content);
            }
        }
        return new ResponseBaseDTO(MsgConstant.SUCCESS, trickers);
    }

    /**
     * 个股公告（内容页）
     */
    @GetMapping("/ticker_notice/content")
    @ResponseBody
    public ResponseBaseDTO tickerNoticeContent(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (id.isEmpty()) return new ResponseBaseDTO("error", "id参数不能为空");
        String content = othersService.selectContentByLc_announcement(id);
        return new ResponseBaseDTO("success", content);
    }
    /**
     * 个股公告（内容页）
     */
    @GetMapping("/ticker_notice/contents")
    @ResponseBody
    public ResponseBaseDTO tickerNoticeContents(@RequestParam("id") String id) {
        NewsVO newsvo = othersService.selectContentsByLc_announcement(id);
        if(null==newsvo)return new ResponseBaseDTO("error","无此新闻", null);
        return new ResponseBaseDTO("success", newsvo);
    }

    /**
     * 个股新闻（个股分析页面)
     */
    @GetMapping("/ticker_news")
    @ResponseBody
    public ResponseBaseDTO tickerNews(HttpServletRequest request) {
        String tickerCode = StringUtil.rmStockCode(request.getParameter("tickerCode"));
        if (StringUtil.isEmpty(tickerCode)) return new ResponseBaseDTO("error", "参数为空或者该股票不存在");
        List<TrickerAnalysis> trickers = othersService.selectTickerNews(tickerCode);
        for (TrickerAnalysis ticker : trickers) {
            String content = ticker.getContent();
            if (content.length() > 100) {
                String shortContent = ticker.getContent().substring(0, 100);
                ticker.setContent(shortContent + "...");
            } else {
                ticker.setContent(content);
            }
        }
        return new ResponseBaseDTO(MsgConstant.SUCCESS, trickers);
    }

    @GetMapping("/ticker_news/content")
    @ResponseBody
    public ResponseBaseDTO tickerNewsContent(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (id.isEmpty()) return new ResponseBaseDTO("error", "id参数不能为空");
        String content = othersService.selectContentByLc_news(id);
        return new ResponseBaseDTO("success", content);
    }

    @GetMapping("/ticker_news/contents")
    @ResponseBody
    public ResponseBaseDTO tickerNewsContent(@RequestParam("id") String id) {
        NewsVO newsvo = othersService.selectTitleContentByLc_news(id);
        if(null==newsvo)return new ResponseBaseDTO("error","无此新闻", null);
        return new ResponseBaseDTO("success", newsvo);
    }

    @PostMapping("/check_token")
    @ResponseBody
    public ResponseBaseDTO checkToken(@RequestBody JSONObject json) {
        if(!json.containsKey("token")) return new ResponseBaseDTO("error","参数有问题，请检查");
        String token=json.getString("token");
        if(null==token||token.isEmpty()) return new ResponseBaseDTO("error","参数有问题，请检查");
        token=token + ".login.token";
        boolean isEXPIRE=false;
        try(Jedis jedis=RedisUtil.getJedis()){
            long time=jedis.pttl(token)/1000;
            if(time<time/600) isEXPIRE=true;
            return new ResponseBaseDTO("success",isEXPIRE+"",time);
        }
    }
}
