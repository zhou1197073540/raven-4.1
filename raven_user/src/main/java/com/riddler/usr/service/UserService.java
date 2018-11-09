package com.riddler.usr.service;

import com.riddler.usr.bean.*;
import com.riddler.usr.common.MsgConstant;
import com.riddler.usr.feign.WalletRemoteAPI;
import com.riddler.usr.mapper.UserMapper;
import com.riddler.usr.utils.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    UserMapper usrMap;

    public User findByUserId(String user_id) {
        return usrMap.findByUserId(user_id);
    }

    public int updateUserInfo(User user) {
        try {
            return usrMap.updateUserInfo(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public User loginVerifyByNP(String phone_num, String passWord) {
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("username", phone_num);
        mapParam.put("password", passWord);
        List<User> listRet = usrMap.loginVerifyNP(mapParam);
        if (listRet.size() > 0)
            return listRet.get(0);
        else
            return null;
    }

    public boolean isExistPhone(String phone_num) {
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("phone_num", phone_num);
        int num = usrMap.isExistPhone(mapParam);
        if (num > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int registerPhone(String phone_num, String passWord, String uid) {
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("phone_num", phone_num);
        mapParam.put("password", passWord);
        mapParam.put("uid", uid);
        mapParam.put("username", "wz_" + phone_num);
        return usrMap.registerPhone(mapParam);
    }

    public boolean insertRecharge(DepositModel deposit) {
        if (usrMap.insertRecharge(deposit) > 0)
            return true;
        return false;
    }

    public UserWalletVO selectUserWalletVoByUid(String user_id) {
        return usrMap.selectUserWalletVoByUid(user_id);
    }

    public Wallet findMoney(String uid) {
        return usrMap.findMoney(uid);
    }

    public boolean checkUsrIsFirstLogin(String uid, String login) {
        String date = TimeUtil.getCurDateTime();
        int num = usrMap.checkUsrIsFirstLogin(uid, date, login);
        if (num <= 0) return false;
        return true;
    }

    public boolean saveUsrAdvise(UsrAdvise usrAdvise) {
        return usrMap.saveUsrAdvise(usrAdvise);
    }

    public boolean checkUsrIsFirstSign(String uid) {
        String date = TimeUtil.getCurDateTime();
        int num = usrMap.checkUsrIsFirstSign(uid, date, MsgConstant.SIGN + "");
        if (num <= 0) return false;
        return true;
    }

    public boolean updateUsrPassword(String phone_num, String password) {
        int num = usrMap.updateUsrPassword(phone_num, password);
        if (num <= 0) {
            return false;
        }
        return true;
    }

    public int isHaveSafe(String uid) throws Exception {
        List<SafeVerify> listRet = usrMap.getOneSafeInfo(uid);
        SafeVerify oneInfo = new SafeVerify();
        if (listRet.size() > 0) {
            if (null == listRet.get(0)) return 1;
        }
        oneInfo = listRet.get(0);
        int count = usrMap.selectRiskLevelCount(uid);
        boolean step1 = StringUtils.isNotBlank(oneInfo.getName()) &&
                StringUtils.isNotBlank(oneInfo.getIdentity_id());
        boolean step2 = StringUtils.isNotBlank(oneInfo.getBank_account());

        if (step1 && step2 && count != 0) return 4;
        else if (step1 && step2 && count == 0) return 3;
        else if (step1 && !step2) return 2;
        else return 1;
    }

    public int addRiskLevelAnswer(Map<String, String> mapParam) throws Exception {
        return usrMap.addRiskAnswer(mapParam);
    }

    public int setNameIdCard(String uid, String name, String idcard) throws Exception {
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("uid", uid);
        mapParam.put("username", name);
        mapParam.put("identity_id", idcard);
        return usrMap.updateNameIdCard(mapParam);
    }

    public int setBankCard(String uid, String bankId) throws Exception {
        Map<String, String> mapParam = new HashMap<String, String>();
        mapParam.put("uid", uid);
        mapParam.put("bank_account", bankId);
        return usrMap.updateBandCard(mapParam);
    }

    public boolean checkPhoneIsExist(String phone_num) {
        int num = usrMap.checkPhoneIsExist(phone_num);
        if (num <= 0) return false;
        return true;
    }

    public List<AccountRecords> getPoint_records_by_comment(String uid, String comment) {
        if (comment.contains("all")) {
            return usrMap.getPoint_all_records(uid);
        } else if (comment.contains("predict")) {
            comment = "'50','6'";
            return usrMap.getPoint_records_by_comment(uid, comment);
        } else if (comment.contains("select_stock")) {
            comment = "'12','13'";
            return usrMap.getPoint_records_by_comment(uid, comment);
        }
        return null;
    }

    public List<AccountRecords> getPoint_records_by_comment_date(String uid, String comment,String start_date,String end_date) {
        System.out.println("============start_date:"+start_date+",end_date:"+end_date);
        if (comment.contains("all")) {
            return usrMap.getPoint_all_records_date(uid,start_date,end_date);
        } else if (comment.contains("predict")) {
            comment = "'50','6'";
            return usrMap.getPoint_records_by_comment_date(uid, comment,start_date,end_date);
        } else if (comment.contains("select_stock")) {
            comment = "'12','13'";
            return usrMap.getPoint_records_by_comment_date(uid, comment,start_date,end_date);
        }
        return null;
    }

    public List<AccountRecords> find_point_record_by_time(String uid, String start_time, String end_time) {
        return usrMap.find_point_record_by_time(uid, start_time, end_time);
    }

    public boolean saveUserImages(String uid, String image_type, String image_urls) {
        String img_type = null;
        if ("0".equals(image_type)) {
            img_type = "portrait_imgs";
        } else if ("1".equals(image_type)) {
            img_type = "id_card_imgs";
        }
        if (null == img_type) return false;
        int num = usrMap.findUserImageByUid(uid);
        if (num > 0) {
            if (usrMap.updateUserImage(uid, img_type, image_urls)) return true;
            System.out.println("图片更新成功");
        } else {
            if (usrMap.insertUserImage(uid, img_type, image_urls) >= 1) return true;
            System.out.println("图片插入成功");
        }
        return false;
    }

    public void updateUserInfoLoginTime(String uid) {
        String time = TimeUtil.getDateTime();
        usrMap.updateUserInfoLoginTime(uid, time);
    }

    public boolean saveReservationConsultation(Order order) {
        if (usrMap.saveReservationConsultation(order) > 0)
            return true;
        return false;
    }

    public int findUserIdByUid(String uid) {
        return usrMap.findUserIdByUid(uid);
    }

    public boolean findIdByWXInfo(int id) {
        int num = usrMap.findIdByWXInfo(id);
        if (num > 0) return true;
        return false;
    }
    public List<OneQuestion> getRiskQA() {
        return usrMap.getRiskQA();
    }
    public int convertAnswer(String ans) {
        if ("a".equals(ans)) return 1;
        else if ("b".equals(ans)) return 2;
        else if ("c".equals(ans)) return 3;
        else if ("d".equals(ans)) return 4;
        else return -1;
    }

    public int addRiskScore(Map<String, String> mapParam) {
        return usrMap.addRiskScore(mapParam);
    }

    public User findUserByUid(String uid) {
        return usrMap.findUserByUid(uid);
    }

    public boolean saveUsrAppointment(Appointment appointment) {
       Integer num= usrMap.saveUsrAppointment(appointment);
       if(num>0) return true;
       return false;
    }
}
