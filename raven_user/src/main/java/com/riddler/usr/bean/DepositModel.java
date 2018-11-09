package com.riddler.usr.bean;

/**
 * Created by lenovo on 2017/6/20.
 */
public class DepositModel {
    private String identify_name;
    private String user_id;
    private String phone_num;
    private String serial_num;
    private String verify_code;//验证码
    private String rmb_change;
    private String create_time;
    private String bank_account;
    private String password;//密码
    private int type;//交易类型
    

    public String getIdentify_name() {
        return identify_name;
    }

    public void setIdentify_name(String identify_name) {
        this.identify_name = identify_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getSerial_num() {
        return serial_num;
    }

    public void setSerial_num(String serial_num) {
        this.serial_num = serial_num;
    }

    public String getVerify_code() {
        return verify_code;
    }

    public void setVerify_code(String verify_code) {
        this.verify_code = verify_code;
    }

    public String getRmb_change() {
        return rmb_change;
    }

    public void setRmb_change(String rmb_change) {
        this.rmb_change = rmb_change;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }


	public String getBank_account() {
		return bank_account;
	}

	public void setBank_account(String bank_account) {
		this.bank_account = bank_account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}


    
}
