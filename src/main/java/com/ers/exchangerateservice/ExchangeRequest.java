package com.ers.exchangerateservice;

public class ExchangeRequest {

	private String request_name;
	private String request_value;

	
	
	public String getRequestName() {
		return request_name;
	}
	
	public String getRequestValue() {
		return request_value;
	}
	
	public void setRequestName(String name) {
		this.request_name=name;
	}
	
	public void setRequestValue(String value) {
		this.request_value=value;
	}
	
	public ExchangeRequest(String name, String value) {
		
		this.request_name=name;
		this.request_value=value;
	}

	
	
}
