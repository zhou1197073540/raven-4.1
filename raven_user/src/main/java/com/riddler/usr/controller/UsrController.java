package com.riddler.usr.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.riddler.usr.annotation.AuthCheck;
import com.riddler.usr.bean.*;
import com.riddler.usr.common.Auth;
import com.riddler.usr.common.Const;
import com.riddler.usr.des.DESUtil;
import com.riddler.usr.des.DesNoViUtil;
import com.riddler.usr.dto.PointsDto;
import com.riddler.usr.dto.RespDto;
import com.riddler.usr.dto.ResponseBaseDTO;
import com.riddler.usr.feign.WalletRemoteAPI;
import com.riddler.usr.service.AsstesService;
import com.riddler.usr.service.UserService;
import com.riddler.usr.common.MsgConstant;
import com.riddler.usr.utils.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import scala.util.parsing.combinator.testing.Str;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static com.riddler.usr.common.Const.V_CODE_EX_TIME;
import static com.riddler.usr.common.Const.RED_SPOT;

@RestController
public class UsrController {
    @Autowired
    UserService userService;
    @Autowired
    WalletRemoteAPI walletApi;
    @Autowired
    TokenUtil tokenUtil;
    @Autowired
    KafkaUtil kafkaUtil;
    @Autowired
    private Environment env;
    @Autowired
    AsstesService assetsService;


    @GetMapping("/usr_center/usr_info")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO getUserInfo(@RequestHeader("Authorization") String token /*HttpServletRequest request*/) {
        ResponseBaseDTO data = new ResponseBaseDTO();
        String uid = tokenUtil.getProperty(token, "uid");
        System.out.println("usr_info uid:" + uid + ",token:" + token);
//        String uid="f5a5804d22335318befd4edcf732f2ee";
//        if (StringUtils.isBlank(uid)) return new ResponseBaseDTO("error", "uid为空");
        User user = userService.findByUserId(uid);
        if (null != user) {
            String[] keys = {"uid", "name", "username", "nickname", "birth", "sex", "phone_num", "mail",
                    "area", "work", "last_login_time", "id_card_imgs", "portrait_imgs"};
            Object[] vals = {user.getUid(), user.getName(), user.getPhone_num(), user.getUsername(), user.getBirth(), user.getSex(),
                    user.getPhone_num(), user.getMail(), user.getArea(), user.getWork(), user.getLast_login_time(),
                    user.getId_card_imgs(), user.getPortrait_imgs()};
            data.setStatus(MsgConstant.SUCCESS);
            data.setData(DataUtil.generateMap(keys, vals));
        } else {
            data.setStatus(MsgConstant.ERROR);
            data.setData("uid不存在");
        }
        return data;
    }

    @PostMapping("/usr_center/update_usr_info")
    @ResponseBody
    public ResponseBaseDTO updateUserInfo(@RequestBody User user) throws Exception {
        String uid = tokenUtil.getProperty(user.getToken(), "uid");
        if (StringUtils.isBlank(uid))
            return new ResponseBaseDTO("error", "uid为空");
        ResponseBaseDTO data = new ResponseBaseDTO();
        user.setUid(uid);
        if (userService.updateUserInfo(user) > 0) {
            User uu=userService.findUserByUid(uid);
            if(uu!=null){
                data.setData(uu.getUsername());
            }
            data.setStatus(MsgConstant.SUCCESS);
            data.setMsg("基本信息更新成功");
        } else {
            data.setStatus(MsgConstant.ERROR);
            data.setData("基本信息更新失败");
        }
        return data;
    }

    /**
     * 用户登录
     * //0,注册,1,充值, 2，提现, 3,卖出,fyb,4,买入fyb, 5,赌博花费 6,赌博赚钱
     */
    @PostMapping("/login/submit")
    @ResponseBody
    public ResponseBaseDTO loginVerify(@RequestBody JSONObject jo) throws Exception {
        ResponseBaseDTO data = new ResponseBaseDTO();
        JSONObject dataJson = jo;
        String phone_num = dataJson.getString("user_name");
        String passWord = dataJson.getString("pass_word");
        User user = userService.loginVerifyByNP(phone_num, passWord);
        if (null != user && StringUtils.isNotBlank(user.getUid())) {
            userService.updateUserInfoLoginTime(user.getUid());//修改上次登录时间
            String token = tokenUtil.createToken(user.getUid());
            System.out.println("登录===================>" + token);
            String[] keys = {"token", "nickname", "username"};
            String[] vals = {token, user.getUsername(), phone_num};
            data.setStatus(MsgConstant.SUCCESS);
            data.setData(DataUtil.generateMap(keys, vals));
            tokenUtil.uploadToken(token, DataUtil.generateMap(new String[]{"uid", "username"}, new String[]{user.getUid(), phone_num}), 7 * 24 * 60 * 60);
            //远程调用wallet送积分   登录不送积分了
//            if (!userService.checkUsrIsFirstLogin(user.getUid(), MsgConstant.LOGIN + "")) {
//                PointsDto point = PointsDto.New()
//                        .setUid(user.getUid())
//                        .setChange(MsgConstant.FIRST_LOGIN_SEND_POINTS)
//                        .setComment(MsgConstant.LOGIN)
//                        .setDate(TimeUtil.getCurDateTime())
//                        .setTime(TimeUtil.getDateTime())
//                        .setSerialNum(HashUtil.generateSerialNum(user.getUid(), MsgConstant.LOGIN + ""));
//                try {
//                    RespDto dto = walletApi.usrSendPoint(point);
//                    if (dto.getStatus().equals(Const.SUCCESS + "")) {
//                        data.setStatus(MsgConstant.SUCCESS);
//                        data.setMsg("登录送积分成功");
//                    } else {
//                        data.setStatus(MsgConstant.REMOTE_ERROR);
//                        data.setMsg("调用钱包返回结果错误，请检查。。");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    data.setStatus(MsgConstant.ERROR);
//                    data.setMsg("调用钱包接口异常，请检查。。。");
//                }
//            }
//        } else {
//            data.setStatus(MsgConstant.ERROR);
//            data.setData("uid为空");
        }
        return data;
    }

    @PostMapping("/login/register")
    @ResponseBody
    public ResponseBaseDTO registerByPhone(@RequestBody JSONObject dataJson) throws Exception {
        ResponseBaseDTO data = new ResponseBaseDTO();
        String phone_num = dataJson.getString("user_name");
        String passWord = dataJson.getString("pass_word");
        String v_code = dataJson.getString("v_code");
        if (!checkPhone_vCode(phone_num, v_code)) return new ResponseBaseDTO(MsgConstant.ERROR, "验证码错误");
        boolean isHave = userService.isExistPhone(phone_num);
        if (!isHave) {
            //0,注册,1,充值, 2，提现, 3,卖出,fyb,4,买入fyb, 5,赌博花费 6,赌博赚钱
            String uid = HashUtil.generateSerialNum(phone_num, "0");
            if (userService.registerPhone(phone_num, passWord, uid) > 0) {//注册成功
                String callbackUrl = "http://"
                        + env.getProperty("spring.application.name")
                        + "/feign/callback";
                PointsDto point = PointsDto.New()
                        .setUid(uid)
                        .setChange(MsgConstant.REGISTER_SEND_POINTS)
                        .setComment(MsgConstant.REGISTER)
                        .setDate(TimeUtil.getCurDateTime())
                        .setTime(TimeUtil.getDateTime())
                        .setOid(HashUtil.generateSerialNum(uid, MsgConstant.REGISTER + ""))
                        .setCallbackUrl(callbackUrl);
                RespDto dto = walletApi.usrSendPoint(point);
                if (dto.getStatus().equals(Const.SUCCESS + "")) {
                    String[] keys = {"uid", "nickname", "username"};
                    Object[] vals = {uid, "wz_" + phone_num, phone_num};
                    data.setStatus(MsgConstant.SUCCESS).setData(DataUtil.generateMap(keys, vals));
                    //直接给用户的资产积分转500，凯哥说的，白版图不好看
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000);
                            PointsDto point_assets = PointsDto.New()
                                    .setUid(uid)
                                    .setChange(MsgConstant.REGISTER_SEND_POINTS)
                                    .setDate(TimeUtil.getCurDateTime())
                                    .setTime(TimeUtil.getDateTime())
                                    .setOid(HashUtil.generateSerialNum(uid, MsgConstant.ASSETS_FROM_POINTS+"")
                                    );
                            RespDto dto1 = walletApi.pointsTransfer(point_assets);
                            System.out.println(dto1.getMsg()+","+ dto1.getData());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                    System.out.println("直接送500资产积分成功");
                } else {
                    data.setStatus(MsgConstant.REMOTE_ERROR).setData("钱包送积分返回值错误，请检查。。");
                }
            }
        } else {
            data.setStatus(MsgConstant.ERROR).setData("已有账号,请换一个帐号重新注册");
        }
        return data;
    }

    public boolean checkPhone_vCode(String phone_num, String p_code) {
        Jedis jedis = RedisUtil.getJedis();
        String redic_pCode = jedis.get(phone_num + "_vcode");
        jedis.close();
        System.out.println("页面传过来的验证码：" + p_code);
        System.out.println("手机号码：" + phone_num);
        System.out.println("redis中的手机验证码：" + redic_pCode);
        if (StringUtils.isNotBlank(p_code) && StringUtils.isNotBlank(redic_pCode) && redic_pCode.equals(p_code)) {
            return true;
        }
        return false;
    }

    /**
     * 接收钱包返回结果
     *
     * @param dto
     * @throws Exception
     */
    @PutMapping("/feign/callback")
    public void walletCallback(@RequestBody JSONObject dto) throws Exception {
        System.out.println("callback!!!");
        System.out.println(dto.getString("status") + dto.getString("msg") + dto.getJSONObject("data").getString("uid"));
        int status = dto.getInteger("status");
        JSONObject td = dto.getJSONObject("data");
        String change = td.getString("change");
        String serialNum = td.getString("serialNum");
        String uid = td.getString("uid");
        String date = td.getString("date");
        String time = td.getString("time");
        int comment = td.getInteger("comment");
        if (100101 != status) {
            //不成功不管
        } else {
            RedSpot rs = new RedSpot();
            rs.setUid(uid);
            rs.setCreateTime(TimeUtil.getDateTime());
            rs.setMsgId(HashUtil.generateSerialNum(uid, String.valueOf(comment)));
            rs.setType(comment);
            if (0 == comment) {//注册
                rs.setMsg("恭喜您注册成功，系统赠送你200积分");
                kafkaUtil.sendMessage(RED_SPOT, JSON.toJSONString(rs));
            } else if (7 == comment) {//登录
                rs.setMsg("恭喜你登录成功，系统赠送送你50积分");
                kafkaUtil.sendMessage(RED_SPOT, JSON.toJSONString(rs));
            } else if (8 == comment) {//签到
                rs.setMsg("恭喜你签到成功，系统赠送送你50积分");
                kafkaUtil.sendMessage(RED_SPOT, JSON.toJSONString(rs));
            }
        }
    }

    /**
     * 生成充值记录
     *
     * @return
     */
    @PutMapping("/recharge/generationRecharge")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO generationRecharge(@RequestBody DepositModel deposit) throws Exception {
        ResponseBaseDTO data = new ResponseBaseDTO();
//        String user_id = "529ca8050a00180790cf88b63468826a";
        String user_id = deposit.getUser_id();
        String serial_num = HashUtil.generateSerialNum(user_id,
                MsgConstant.RECHARGE + "");
        deposit.setSerial_num(serial_num);
        deposit.setUser_id(user_id);
        deposit.setType(MsgConstant.RECHARGE);
        deposit.setRmb_change("+" + deposit.getRmb_change());
        deposit.setCreate_time(TimeUtil.getDateTime());
        if (userService.insertRecharge(deposit)) {
            data.setStatus(MsgConstant.SUCCESS);
        } else
            data.setStatus(MsgConstant.ERROR);
        return data;
    }

    @GetMapping("/recharge/generationRechargeBefore")
    @ResponseBody
    public ResponseBaseDTO generationRechargeBefore() {
        ResponseBaseDTO data = new ResponseBaseDTO();
        String[] keys = {"amount_suffix", "verify_code"};
        Object[] vals = {HashUtil.generateTwoRandom(),
                HashUtil.generateRandomStr(4)};
        data.setStatus(MsgConstant.SUCCESS);
        data.setData(DataUtil.generateMap(keys, vals));
        return data;
    }

    /**
     * 生成提现记录
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/withdrawals/generationWithdrawals")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO generationWithdrawals(@RequestBody DepositModel deposit) throws Exception {
        ResponseBaseDTO data = new ResponseBaseDTO();
//        String user_id="529ca8050a00180790cf88b63468826a";
        String user_id = deposit.getUser_id();
        Jedis jedis = RedisUtil.getJedis();
        String phone_code = jedis.get(user_id + "_code");//从redis中获取用户生成的手机验证码
        jedis.close();
        //对手机验证码进行 校验
//    	if(StringUtils.isBlank(phone_code)||phone_code.equals(deposit.getVerify_code())){
//    		data.setStatus(MsgConstant.SUCCESS);
//    		data.setData("您的验证码失效或者不正确！！！");
//    		return data;
//    	}
        UserWalletVO vo = userService.selectUserWalletVoByUid(user_id);
        if (null == vo) {
            data.setStatus(MsgConstant.ERROR);
            data.setData("该用户不存在或该用户信息不完整！！！");
            return data;
        }
        if (!deposit.getPassword().equals(vo.getWallet_pwd())) {
            data.setStatus(MsgConstant.ERROR);
            data.setData("支付密码不对，请重新输入！！！");
            return data;
        }
        if (Integer.parseInt(deposit.getRmb_change()) > Integer.parseInt(vo.getRmb())) {
            data.setStatus(MsgConstant.ERROR);
            data.setData("您提现的余额不足，请重新输入！！！");
            return data;
        }
        deposit.setIdentify_name(vo.getName());
        deposit.setPhone_num(vo.getPhone_num());
        String serial_num = HashUtil.generateSerialNum(user_id, MsgConstant.WITHDRAWALS + "");
        deposit.setSerial_num(serial_num);
        deposit.setUser_id(user_id);
        deposit.setType(MsgConstant.WITHDRAWALS);
        deposit.setRmb_change("-" + deposit.getRmb_change());
        deposit.setCreate_time(TimeUtil.getDateTime());
        if (userService.insertRecharge(deposit)) {
            data.setStatus(MsgConstant.SUCCESS);
        } else data.setStatus(MsgConstant.ERROR);
        return data;
    }

    @PostMapping("/withdrawals/generationWithdrawalsBefore")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO generationWithdrawalsBefore(@RequestHeader("Authorization") String token) throws Exception {
        ResponseBaseDTO data = new ResponseBaseDTO();
//        String user_id = "529ca8050a00180790cf88b63468826a";
        String uid = tokenUtil.getProperty(token, "uid");
        User user = userService.findByUserId(uid);
        if (null != user) {
            if (StringUtils.isBlank(user.getBank_account())) {
                data.setStatus(MsgConstant.ERROR);
                data.setData("此用户没有银行卡号！！！");
            } else {
                String[] banks = user.getBank_account().split(";");
                data.setStatus(MsgConstant.SUCCESS);
                data.setData(banks);
            }
        }
        return data;
    }

    /**
     * 签到送积分
     */
    @GetMapping("/signSendPoints")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO signSendPoints(@RequestHeader("Authorization") String token) throws Exception {
        if (StringUtils.isBlank(token)) return null;
        String uid = tokenUtil.getProperty(token, "uid");
        String callbackUrl = "http://"
                + env.getProperty("spring.application.name")
                + "/feign/callback";
        PointsDto point = PointsDto.New()
                .setUid(uid)
                .setChange(MsgConstant.SIGN_SEND_POINTS)
                .setComment(MsgConstant.SIGN)
                .setDate(TimeUtil.getCurDateTime())
                .setTime(TimeUtil.getDateTime())
                .setOid(HashUtil.generateSerialNum(token, MsgConstant.SIGN + ""))
                .setCallbackUrl(callbackUrl);
        ResponseBaseDTO data = new ResponseBaseDTO();
        if (!userService.checkUsrIsFirstSign(uid)) {
            RespDto dto = walletApi.usrSendPoint(point);
            if ("100101".equals(dto.getStatus())) {//成功
                data.setStatus(MsgConstant.SUCCESS).setData("签到成功");
            } else {
                data.setStatus(MsgConstant.REMOTE_ERROR).setData("调用钱包错误，请检查。。");
            }
        } else {
            data.setStatus(MsgConstant.ERROR).setData("您已经签过到了");
        }
        return data;
    }


    /**
     * 查询可用积分
     */
    @GetMapping("/findActivePoints")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO findActivePoints(@RequestHeader("Authorization") String token) throws Exception {
        if (StringUtils.isBlank(token)) return null;
        String uid = tokenUtil.getProperty(token, "uid");
        RespDto dto = walletApi.getActivePoints(uid);

        if ("100101".equals(dto.getStatus())) {//成功
            return new ResponseBaseDTO(MsgConstant.SUCCESS, dto.getData());
        }
        return new ResponseBaseDTO(MsgConstant.ERROR, dto.getMsg());
    }

    /**
     * 查询投资积分
     */
    @GetMapping("/findInvestPoints")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO findInvestPoints(@RequestHeader("Authorization") String token) throws Exception {
        if (StringUtils.isBlank(token)) return null;
        String uid = tokenUtil.getProperty(token, "uid");
        RespDto dto = walletApi.getUsrPoints(uid);

        if ("100101".equals(dto.getStatus())) {//成功
            return new ResponseBaseDTO(MsgConstant.SUCCESS, dto.getData());
        }
        return new ResponseBaseDTO(MsgConstant.ERROR, dto.getMsg());
    }

    /**
     * 查询用户当前可用余额
     */
    @GetMapping("/findUsrRmb")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO findUsrRmb(@RequestHeader("Authorization") String token) throws Exception {
        if (StringUtils.isBlank(token)) return null;
        String uid = tokenUtil.getProperty(token, "uid");

        RespDto dto = walletApi.getUsrRmb(uid);
        if ("100101".equals(dto.getStatus())) {//成功
            return new ResponseBaseDTO(MsgConstant.SUCCESS, dto.getData());
        }
        return new ResponseBaseDTO(MsgConstant.ERROR, dto.getMsg());
    }

    /**
     * 查询用户人民币，可用积分，投资积分和 美元汇率
     */
    @GetMapping("/assets/rmb_point_rate")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO get_rmb_point_rate(@RequestHeader("Authorization") String token) throws Exception {
        String uid = tokenUtil.getProperty(token, "uid");
        if (StringUtils.isBlank(uid)) return null;
        Object rmb = 0, active_points = 0, assets_points = 0, rate = 0;
        //查询人民币
        RespDto rmb_dto = walletApi.getUsrRmb(uid);
        if ("100101".equals(rmb_dto.getStatus())) {//成功
            rmb = rmb_dto.getData();
        }
        //查询投资积分
        RespDto touzi_dto = walletApi.getUsrPoints(uid);
        if ("100101".equals(touzi_dto.getStatus())) {//成功
            assets_points = touzi_dto.getData();
        }
        //查询可用积分
        RespDto active_dto = walletApi.getActivePoints(uid);
        if ("100101".equals(active_dto.getStatus())) {//成功
            active_points = active_dto.getData();
        }
        //查询人民币汇率
        rate = assetsService.getRmbRate();
        String[] keys = {"rmb", "active_points", "assets_points", "rate"};
        Object[] vals = {rmb, active_points, assets_points, rate};

        return new ResponseBaseDTO(MsgConstant.SUCCESS, DataUtil.generateMap(keys, vals));
    }


    /**
     * 用户建议
     */
    @PostMapping("/usrAdvise")
    @ResponseBody
    public ResponseBaseDTO signSendPoints(@RequestBody UsrAdvise usrAdvise)
            throws ServletException, IOException {
        ResponseBaseDTO dto = ResponseBaseDTO.New();
        if (usrAdvise.getUser_name().isEmpty()) {
            return dto.setData("用户名不能为空").setStatus(MsgConstant.ERROR);
        }
        if (userService.saveUsrAdvise(usrAdvise)) {
            return dto.setData("保存成功").setStatus(MsgConstant.SUCCESS);
        }
        return dto.setData("保存出错，报告程序员解决。。").setStatus(MsgConstant.ERROR);
    }

    /**
     * 找回密码的第三步，修改密码
     */
    @ResponseBody
    @PostMapping("/update_pwd")
    public ResponseBaseDTO update_pwd(@RequestBody JSONObject json) throws Exception {
        String phone_num = json.getString("phone_num");
        String password = json.getString("password");
//        System.out.println("电话+密码："+phone_num+":"+password);
        if (null == phone_num || null == password) return new ResponseBaseDTO(MsgConstant.ERROR, "密码不能为空");
        if (userService.updateUsrPassword(phone_num, password)) {
            return new ResponseBaseDTO(MsgConstant.SUCCESS, "更新成功");
        }
        return new ResponseBaseDTO(MsgConstant.ERROR, "更新异常");
    }

    /**
     * 找回密码的第二步，提交接口
     * 只要验证手机发送的验证码即可
     */
    @ResponseBody
    @PostMapping("/checkPhoneCode")
    public ResponseBaseDTO check_pCode(@RequestBody JSONObject json) throws Exception {
        String phone_num = json.getString("phone_num");
        String p_code = json.getString("v_code");//手机验证码
        if (phone_num.isEmpty() || p_code.isEmpty()) {
            return new ResponseBaseDTO(MsgConstant.ERROR, "手机号或者验证码不正确");
        }
        Jedis jedis = RedisUtil.getJedis();
        String redic_pCode = jedis.get(phone_num + "_vcode");
        jedis.close();
//        System.out.println("输入的手机验证码："+p_code+" ，redis中的验证码："+redic_pCode);
        if (null != p_code && null != redic_pCode && redic_pCode.equals(p_code)) {
            return new ResponseBaseDTO(MsgConstant.SUCCESS, "验证码正确");
        }
        return new ResponseBaseDTO(MsgConstant.ERROR, "手机号或者验证码不正确");
    }


    @GetMapping("/usr_center/is_have_safe")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO isHaveSafeVerify(HttpServletRequest request) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String token = request.getHeader("Authorization");
        String uid = tokenUtil.getProperty(token, "uid");
        System.out.println("recive uid : " + uid);
        if (uid == null) {
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setData("need login");
        } else {
            int flag = userService.isHaveSafe(uid);
            if (flag == 4) {
                responseBaseDTO.setStatus(MsgConstant.SUCCESS);
                JSONObject resJo = new JSONObject();
                resJo.put("isDone", true);
                resJo.put("step", null);
                responseBaseDTO.setData(resJo);
                responseBaseDTO.setMsg("已经进行了所有的安全认证");
            } else {
                responseBaseDTO.setStatus(MsgConstant.SUCCESS);
                JSONObject resJo = new JSONObject();
                resJo.put("isDone", false);
                resJo.put("step", String.valueOf(flag));
                responseBaseDTO.setData(resJo);
                responseBaseDTO.setMsg("安全认证并没有做完");
            }
        }
        return responseBaseDTO;
    }

    @PostMapping("/usr_center/risk_level")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO reviceRiskLevelAnswer(@RequestBody JSONObject jo,
                                                 HttpServletRequest request) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String token = request.getHeader("Authorization");
        String uid = tokenUtil.getProperty(token, "uid");
        System.out.println("recive uid : " + uid);
        JSONObject data = jo.getJSONObject("data");
        String answer = data.getString("answer");
        String format = answer.replace("a", "").replace("b", "")
                .replace("c", "").replace("d", "")
                .replace("e", "").replace(",", "");
        if (StringUtils.isBlank(format) && answer.split(",", -1).length == 10) {
            // insert
            String timeStr = TimeUtil.getDateTime();
            Map<String, String> mapParam = new HashMap<String, String>();
            mapParam.put("user_id", uid);
            mapParam.put("create_time", timeStr);
            mapParam.put("answer", answer);
            userService.addRiskLevelAnswer(mapParam);
            responseBaseDTO.setStatus(MsgConstant.SUCCESS);
            responseBaseDTO.setData("操作成功");
        } else {
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setData("操作失败");
        }
        return responseBaseDTO;
    }

    @PostMapping("/usr_center/set_name_idcard")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO setNameIdCard(@RequestBody JSONObject jo,
                                         HttpServletRequest request) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String token = request.getHeader("Authorization");
        String uid = tokenUtil.getProperty(token, "uid");
        System.out.println("recive uid : " + uid);
        JSONObject data = jo.getJSONObject("data");
        String name = data.getString("name");
        String idcard = data.getString("idcard");
        User user = userService.findByUserId(uid);
        if (user != null) {
            int ret = userService.setNameIdCard(uid, name, idcard);
            if (ret == 1) {
                responseBaseDTO.setData("操作成功");
                responseBaseDTO.setStatus(MsgConstant.SUCCESS);
            } else {
                responseBaseDTO.setData("操作失败");
                responseBaseDTO.setStatus(MsgConstant.ERROR);
            }
        } else {
            responseBaseDTO.setData("操作失败");
            responseBaseDTO.setStatus(MsgConstant.ERROR);
        }
        return responseBaseDTO;
    }

    @PostMapping("/usr_center/set_bank_card")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO setBankCard(@RequestBody JSONObject jo,
                                       HttpServletRequest request) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String token = request.getHeader("Authorization");
        String uid = tokenUtil.getProperty(token, "uid");
        System.out.println("recive uid : " + uid);
        JSONObject data = jo.getJSONObject("data");
        String bankName = data.getString("bank_name");
        String bankId = data.getString("bank_id");
        String vCode = data.getString("v_code");
        String phoneNum = data.getString("phone_num");
        //TODO vCode 验证 redis
        Jedis jedis = RedisUtil.getJedis();
        if (!jedis.exists(phoneNum + "_vcode")) {
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setMsg("验证码超时");
        } else {
            String oriCode = jedis.get(phoneNum + "_vcode");
            if (vCode.equals(oriCode)) {
                int ret = userService.setBankCard(uid, bankId);
                if (ret == 1) {
                    responseBaseDTO.setData("操作成功");
                    responseBaseDTO.setStatus(MsgConstant.SUCCESS);
                } else {
                    responseBaseDTO.setData("服务器异常");
                    responseBaseDTO.setStatus(MsgConstant.ERROR);
                }
            } else {
                responseBaseDTO.setData("验证码错误");
                responseBaseDTO.setStatus(MsgConstant.ERROR);
            }
        }
        return responseBaseDTO;
    }

    @PostMapping("/verify/v_code")
    @ResponseBody
    public ResponseBaseDTO getVcode(@RequestBody JSONObject jo) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        JSONObject data = jo.getJSONObject("data");
        String phoneDesStr = data.getString("phone_num");
        String platform = data.getString("platform");
        if(phoneDesStr.length()==11)return new ResponseBaseDTO("error","非法字符串！！");
        String phone_date=null;
        try {
            if(platform!=null && "wx_mini".equals(platform)){
                //微信小程序的加密方式
                phone_date= new DesNoViUtil().decode(phoneDesStr);
                System.out.println("小程序加密:"+phone_date);
            }else{
                phone_date= DESUtil.decryption(phoneDesStr, Const.SECRET_KEY);
                System.out.println("其他平台加密:"+phone_date);
            }
        }catch (Exception e){
            System.out.println("这是一个非法字符串:"+phoneDesStr);
        }
        if(StringUtil.isEmpty(phone_date)) return new ResponseBaseDTO("error","非法字符串");
        String[] strs=phone_date.split("-");
        String phone_num=strs[0];
        long appCurentMill=Long.parseLong(strs[1]);
        long ss=System.currentTimeMillis()-appCurentMill;
        System.out.println("时间间隔："+ss/1000 +"秒");
        if(ss>=30*1000){
            return new ResponseBaseDTO("error","传入的字符已经过时！"+appCurentMill);
        }
        System.out.println("recive msg:"+phoneDesStr+" ,decryption phone_num for v_code: " + phone_num);
        if (StringUtils.isBlank(phone_num)) {
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setData("phone_num empty");
        } else {
            responseBaseDTO = doSendMsg(phone_num);
        }
        return responseBaseDTO;
    }

    public ResponseBaseDTO doSendMsg(String phoneNum) throws Exception {
        ResponseBaseDTO responseBaseDTO = new ResponseBaseDTO();
        String randomNum = HashUtil.generateRandomNum(6);
        System.out.println("randomNum : " + randomNum);
        Jedis jedis = RedisUtil.getJedis();
        jedis.setex(phoneNum + "_vcode", V_CODE_EX_TIME, randomNum);
        jedis.close();
        int ret = SmsUtil.singleSend(randomNum, phoneNum);
        System.out.println("ret : " + ret);
        if (ret == 0) {
            responseBaseDTO.setStatus(MsgConstant.SUCCESS);
            responseBaseDTO.setData("验证码正在发送中,请稍后...");
        } else if (ret == 33) {
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setData("30秒内只能发送一次，请30秒后再试");
        } else if (ret == 22) {
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setData("1小时内只能发送3次，请1小时后再试");
        }else if(ret == 3){
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setData("账户钱没了，赶紧看看是不是又有别人在攻击验证码接口了！！");
        }else if(ret == 17){
            responseBaseDTO.setStatus(MsgConstant.ERROR);
            responseBaseDTO.setData("24小时内同一手机号,发送次数不能超过10次 ");
        }
        return responseBaseDTO;
    }

    /**
     * 我的账户中的积分记录
     */
    @PostMapping("/account/point_records")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO getPoint_records(@RequestHeader("Authorization") String token, @RequestBody JSONObject jo) throws Exception {
        String uid = tokenUtil.getProperty(token, "uid");
        if (StringUtils.isBlank(uid)) return new ResponseBaseDTO("error", "uid为空");
        List<AccountRecords> list=null;
        String comment = jo.getString("comment");
        if(jo.containsKey("start_date")&&jo.containsKey("end_date")){
            String start_date = jo.getString("start_date");
            String end_date = jo.getString("end_date");
            list = userService.getPoint_records_by_comment_date(uid, comment,start_date,end_date);
        }else{
            list = userService.getPoint_records_by_comment(uid, comment);
        }
        for (AccountRecords record : list) {
            String remark = DataUtil.formatComment(record.getComment());
            if (record.getChange().contains("-")) {
                record.setStatus("消费积分");
            } else {
                record.setStatus("获取积分");
            }
            String time = record.getCreate_time();
            if (time.contains(".")) {
                time = time.substring(0, time.indexOf("."));
                record.setCreate_time(time);
            }
            record.setRemarks(remark);
        }
        return new ResponseBaseDTO(MsgConstant.SUCCESS, list);
    }

    /**
     * 我的账户中的根据时间查询用户积分记录
     */
    @PostMapping("/account/find_point_records_by_time")
    @ResponseBody
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO find_point_records_by_time(@RequestHeader("Authorization") String token, @RequestBody JSONObject jo) throws Exception {
        String uid = tokenUtil.getProperty(token, "uid");
        if (StringUtils.isBlank(uid)) return new ResponseBaseDTO("error", "uid为空");
        String start_time = jo.getString("start_time");
        String end_time = jo.getString("end_time");
        List<AccountRecords> list = userService.find_point_record_by_time(uid, start_time, end_time);
        return new ResponseBaseDTO(MsgConstant.SUCCESS, list);
    }

    /**
     * 保存图片
     * 0:头像图片，1：身份证图片
     */
    @ResponseBody
    @PostMapping("/saveImages")
    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO save_Images(@RequestHeader("Authorization") String token, @RequestBody JSONObject json) {
        String image_url = json.getString("image_base64_url");
        String image_type = json.getString("image_type");
        String uid = tokenUtil.getProperty(token, "uid");
//        String uid="f5a5804d22335318befd4edcf732f2ee";
        if (StringUtils.isBlank(uid)) return new ResponseBaseDTO("error", "uid为空");
        if (!userService.saveUserImages(uid, image_type, image_url)) {
            return new ResponseBaseDTO(MsgConstant.ERROR, "图片存入失败");
        }
        return new ResponseBaseDTO(MsgConstant.SUCCESS, "图片存入成功");
    }

    /**
     * 预约咨询
     */
    @ResponseBody
    @PostMapping("/reservationConsultation")
    public ResponseBaseDTO reservationConsultation(@RequestBody Order order) {
        if (!userService.saveReservationConsultation(order)) {
            return new ResponseBaseDTO(MsgConstant.ERROR, "预约咨询失败");
        }
        return new ResponseBaseDTO(MsgConstant.SUCCESS, "预约咨询成功");
    }

    @ResponseBody
    @GetMapping("/risk/level/qa")
//    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO getRiskQA() {
        List<OneQuestion> listQA = userService.getRiskQA();
        int length = listQA.size();
        JSONObject jo = new JSONObject();
        for (int i = 1; i <= length; i++) {
            Map<String, String> oneQA = new HashMap<String, String>();
            oneQA.put("question", listQA.get(i - 1).getQuestion());
            oneQA.put("answer", listQA.get(i - 1).getAnswer());
            jo.put(String.valueOf(i), oneQA);
        }
        return new ResponseBaseDTO(MsgConstant.SUCCESS, "返回问答成功", jo);
    }

    @ResponseBody
    @PostMapping("/risk/level/receive")
//    @AuthCheck(Auth.NEED_LOGIN)
    public ResponseBaseDTO receiveRiskA(@RequestBody JSONObject jo) {
//        暂时先不用token，临时这样。。
//        String uid = tokenUtil.getProperty(token, "uid");
        String answerStr = jo.getString("answer");
        String[] answerArr = answerStr.split(",", -1);
        List<OneQuestion> listQA = userService.getRiskQA();
        int length = listQA.size();
        if (answerArr.length != length) {
            return new ResponseBaseDTO(MsgConstant.ERROR, "答题数量不完整");
        } else {
            double greedyS = 0.0;
            double fearS = 0.0;
            for (int i = 0; i < length; i++) {
                if (1 == listQA.get(i).getGreedy()) {
                    int x = userService.convertAnswer(answerArr[i]);
                    if (-1 == x) return new ResponseBaseDTO(MsgConstant.ERROR, "答案含有非法选项");
                    else greedyS += listQA.get(i).getScore() * x;
                }
                if (1 == listQA.get(i).getFear()) {
                    int y = userService.convertAnswer(answerArr[i]);
                    if (-1 == y) return new ResponseBaseDTO(MsgConstant.ERROR, "答案含有非法选项");
                    else fearS += listQA.get(i).getScore() * y;
                }
            }
            System.out.println("greed :" + greedyS);
            System.out.println("fear : " + fearS);
            Map<String, String> mapParam = new HashMap<String, String>();
            //暂时这样，还没有做移动端的账户
            mapParam.put("uid", HashUtil.generateSerialNum("linshi", "666"));
            mapParam.put("answer", answerStr);
            mapParam.put("greedy", String.valueOf(greedyS));
            mapParam.put("fear", String.valueOf(fearS));
            String curTime = TimeUtil.getDateTime();
            mapParam.put("update_time", curTime);
            mapParam.put("create_time", curTime);
            int ret = userService.addRiskScore(mapParam);
            if (1 == ret) return new ResponseBaseDTO(MsgConstant.SUCCESS, "提交成功");
            else return new ResponseBaseDTO(MsgConstant.ERROR, "服务器内部错误");
        }
    }

    /**
     * 用户建议
     */
    @PostMapping("/usrAppointment")
    @ResponseBody
    public ResponseBaseDTO signSendPoints(@RequestBody Appointment appointment){
        ResponseBaseDTO dto = ResponseBaseDTO.New();
        if (appointment.getPhone_number().trim().isEmpty()) {
            return dto.setData("手机号不能为空").setStatus(MsgConstant.ERROR);
        }
        if (userService.saveUsrAppointment(appointment)) {
            return dto.setData("保存成功").setStatus(MsgConstant.SUCCESS);
        }
        return dto.setData("保存出错，报告程序员解决。。").setStatus(MsgConstant.ERROR);
    }
}
