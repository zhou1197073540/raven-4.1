package com.raven.bean;
/**
 *机器人的预测结果实体 
 */
public class PredictVal {
	private String datetime;
	private float dnprob;	//下降概率
	private float nowindex;	//当前指数
	private float predindex; //预测指数
	private String ticker;
	private float upprob;	//上升概率
	
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public float getDnprob() {
		return dnprob;
	}
	public void setDnprob(float dnprob) {
		this.dnprob = dnprob;
	}
	public float getNowindex() {
		return nowindex;
	}
	public void setNowindex(float nowindex) {
		this.nowindex = nowindex;
	}
	public float getPredindex() {
		return predindex;
	}
	public void setPredindex(float predindex) {
		this.predindex = predindex;
	}
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public float getUpprob() {
		return upprob;
	}
	public void setUpprob(float upprob) {
		this.upprob = upprob;
	}
	
}
