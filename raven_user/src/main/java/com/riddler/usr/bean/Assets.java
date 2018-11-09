package com.riddler.usr.bean;

public class Assets {
	private String assets_code;
	private float weight;
	private String unit;
	private int amount;
	private String buy_time;
    private String  buy_time_money;//买入时单个etf的总价钱
    private String buy_time_price;//买入时单个etf的单价
    private float lastest_price; //etf最新价格

	public String getAssets_code() {
		return assets_code;
	}
	public void setAssets_code(String assets_code) {
		this.assets_code = assets_code;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public float getLastest_price() {
		return lastest_price;
	}
	public void setLastest_price(float lastest_price) {
		this.lastest_price = lastest_price;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}

    public String getBuy_time_price() {
        return buy_time_price;
    }

    public void setBuy_time_price(String buy_time_price) {
        this.buy_time_price = buy_time_price;
    }

    public String getBuy_time() {
        return buy_time;
    }

    public void setBuy_time(String buy_time) {
        this.buy_time = buy_time;
    }

    public String getBuy_time_money() {
        return buy_time_money;
    }

    public void setBuy_time_money(String buy_time_money) {
        this.buy_time_money = buy_time_money;
    }
}
