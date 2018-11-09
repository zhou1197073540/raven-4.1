package com.raven.dto;

public class ResponseBaseDTO {
	private String status;
	private Object data;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public ResponseBaseDTO(String status, Object data) {
		super();
		this.status = status;
		this.data = data;
	}

	public ResponseBaseDTO() {
		super();
	}
	
	public void clear(){
		this.status=null;
		this.data=null;
	}

}
