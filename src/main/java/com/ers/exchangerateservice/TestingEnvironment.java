package com.ers.exchangerateservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest
public class TestingEnvironment {

	
	@Autowired
	private TestRestTemplate exchangeRequestTest;

	@Test
	public void initialCheck() throws Exception { 
		
		assertThat(this.exchangeRequestTest.getForObject(
				"http://localhost:8080/chart/usd-gbp",String.class) )
		.contains("{\"requestName\":\"public_web_link\",\"requestValue\":\"https://www.xe.com/currencycharts/?from=USD&to=GBP\"}");}
	

}
