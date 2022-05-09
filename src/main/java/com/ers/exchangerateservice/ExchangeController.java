package com.ers.exchangerateservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.net.URI;
import java.net.URL;
import org.xml.sax.InputSource;

@RestController
public class ExchangeController {
	
	
	HashMap<String,String> ecb_currency_rates =  new HashMap<String,String>();
	
	HashMap<String,Integer> ecb_currency_names_requests =  new HashMap<String,Integer>();
	
	
	
	/**
	 * The functions returns the rate of a currency against the euro
	 * @param /euro-rate/usd
	 * return  USD/EUR rate 0.95
	 * The function also increments the number of requests for the @param currency  
	 * */
	@RequestMapping(value={"/euro-rate/{currency}","/pair-rate/{currency}-eur"})
	public ExchangeRequest euroExchangeRequest(@PathVariable( "currency" ) String currency) {
	
		//increment request count of requested currencies
		
		increment_request_count(currency);
		/////////////////////////////////////////////////	
				
		double currency_rate_against_euro=
				1/(Double.parseDouble(ecb_currency_rates.get(currency.toUpperCase())));
			
		return new ExchangeRequest(String.format("%s_euro_ref_rate", currency),String.valueOf(currency_rate_against_euro));
		
	}
	
	
	/**
	 * The functions returns the rate of the euro against the currency
	 * @param /currency-rate/usd
	 * return  EUR/USD rate  1.05
	 * The function also increments the number of requests for the @param currency  
	 * */
	@RequestMapping(value={"/currency-rate/{currency}","/pair-rate/eur-{currency}"})
	public ExchangeRequest currencyExchangeRequest(@PathVariable( "currency" ) String currency) {
		
		//increment request count of requested currencies
		increment_request_count(currency);
		/////////////////////////////////////////////////	
		
		return new ExchangeRequest(String.format("euro_%s_ref_rate", currency),ecb_currency_rates.get(currency.toUpperCase()));
		
	}
	
	/**
	 * Gives a list of all the currencies show on the ECB website as referenced in the assesment sheet
	 * @param none
	 * return total number currencies, accronyms of currencies, the number of requests for each currency
	 * */
	@RequestMapping("/list")
	public List<ExchangeRequest> listExchangeRequest() {
		List<ExchangeRequest> return_list = new ArrayList<ExchangeRequest>();		
		return_list.add(new ExchangeRequest("Total currencies",String.valueOf(ecb_currency_names_requests.size()-1 )));
		
		for(String name:ecb_currency_names_requests.keySet()) {
			return_list.add(new ExchangeRequest(name,String.valueOf(ecb_currency_names_requests.get(name))));			
		}
		return return_list;
		
	}
	
	
	/**
	 * Gives the rate of conversion between two currencies
	 * @param String currency_from  USD
	 * @param String currency_to CHF
	 * 
	 * return USD/CHF rate 
	 * 
	 * Both of the requested currencies' requests is incremented by 1
	 * */
	
	@RequestMapping("/pair-rate/{currency_from}-{currency_to}")
	public ExchangeRequest pairExchangeRequest(
			@PathVariable( "currency_from" ) String currency_from,
			@PathVariable( "currency_to" ) String currency_to) {
		
		String rate_return="";
		
		if(currency_to.toUpperCase()==currency_from.toUpperCase()) {
			rate_return="1";
			
			//increment request count of requested currencies
			increment_request_count(currency_from);
		}
		else {
		
			rate_return= String.valueOf( convertCurrencies(1, currency_from, currency_to) );
			
			//increment request count of requested currencies
			increment_request_count(currency_from);
			increment_request_count(currency_to);
		}
		
		return new ExchangeRequest(
						String.format("%s_%s_rate", currency_from,currency_to),
						rate_return
						);
		
	}
	
	
	/**
	 * Functions as a currency converter where an amount from one currency is converted to another currency
	 * @param currency_amount 15 
	 * @param currency_from EUR
	 * @param currency_to GBP
	 * return 15 EUR => 12.76 GBP
	 * 
	 * */
	@RequestMapping("/currency-converter/{currency_amount}-{currency_from}-{currency_to}")
	public ExchangeRequest pairExchangeRequest(
			@PathVariable( "currency_amount" ) String currency_amount,
			@PathVariable( "currency_from" ) String currency_from,
			@PathVariable( "currency_to" ) String currency_to) {
		
		
	return new ExchangeRequest(
			String.format("%s_%s_%s_amount", currency_amount,currency_from,currency_to),
			String.valueOf(convertCurrencies(Double.parseDouble(currency_amount), currency_from, currency_to) )
			);	
	}
	
	/**
	 * Returns a link of a public website which to see an interactive chart of the requested currencies
	 * @param currency_from USD
	 * @param currency_to GBP
	 * return web-link/chart/USD-GBP
	 * */
	@RequestMapping("/chart/{currency_from}-{currency_to}")
	public ExchangeRequest chartExchangeRequest(
			@PathVariable( "currency_from" ) String currency_from,
			@PathVariable( "currency_to" ) String currency_to) {
		
		
	return new ExchangeRequest(
			"public_web_link",
			String.format("https://www.xe.com/currencycharts/?from=%s&to=%s",currency_from.toUpperCase(),currency_to.toUpperCase())
			);	
	}
	
	/**
	 * Automatically redirects to a public website to see an interactive chart of the requested currencies
	 * @param currency_from USD
	 * @param currency_to GBP
	 * Redirect to public website
	 * */
	@RequestMapping("/chart-open/{currency_from}-{currency_to}")
    public ResponseEntity<Void> redirect(
    		@PathVariable( "currency_from" ) String currency_from,
    		@PathVariable( "currency_to" ) String currency_to){
 
        return ResponseEntity.status(HttpStatus.FOUND).location(
        		URI.create(String.format("https://www.xe.com/currencycharts/?from=%s&to=%s",currency_from.toUpperCase(),currency_to.toUpperCase()))
        		).build();
    }
	
	/**Xml file is mapped to Hashmap periodically after 3 seconds (3000 milliseconds)*/
	@Async
	@Scheduled(fixedDelay=3000)
	private void mapXmlFileToHashMap(){
		
		map_ecb_rates_from_url_xml();
	
	}
	
	
	/**
	 * Function to convert an amount from one currency to another
	 * @param currency_amount 15 
	 * @param currency_from EUR
	 * @param currency_to GBP
	 * return currency rate
	 * */
	
	public double convertCurrencies(double amount, String currency_from, String currency_to) {
		
		double currency_rate=(amount * getDouble(currency_to))/getDouble(currency_from);
		
		return currency_rate;
	}
	
	
	
	/**
	 * Increment the requests for requested currency in HashMap with names and requests
	 * @param String currency
	 * **/
	public void increment_request_count(String currency) {
		
		if(currency.toUpperCase() !="EUR") {
			ecb_currency_names_requests.put(
				currency.toUpperCase(),
				ecb_currency_names_requests.get(currency.toUpperCase())+1
				);
		}

	}
	
	/**
	 * Parse from currency name to euro-rate double 
	 * @param String currency
	 * */
	public double getDouble(String currency) {
		return Double.parseDouble(ecb_currency_rates.get(currency.toUpperCase()));
	}
	
	/**
	 * Download the xml ecb file from Ecb referenced website
	 * and map the currency names and rates into HashMaps
	 * */
	public void map_ecb_rates_from_url_xml() {
		
		
		 try {
	         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	         DocumentBuilder builder = factory.newDocumentBuilder();
	         
	         Document document = builder.parse(new InputSource(new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml?d17e13451d1394877b798b5026c64e26").openStream()));
	         
	         
	         NodeList xml_nodes=document.getElementsByTagName("Cube");
	         
	         for(int i = 0;i<xml_nodes.getLength();i++ ) {
	        	 
	        	 NamedNodeMap node_attributes=xml_nodes.item(i).getAttributes();
	        	 
	        	 if(node_attributes.getNamedItem("rate")!=null) {
	        
	        		 String currency_name=node_attributes.getNamedItem("currency").getNodeValue().toUpperCase();
	        		 
	        		 if(ecb_currency_names_requests.get(currency_name)==null) {
	        			 ecb_currency_names_requests.put(currency_name, 0);	 
	        		 }
	        		 
	        		 
	        		 ecb_currency_rates.put(
	        				 currency_name, 
	        			 	 node_attributes.getNamedItem("rate").getNodeValue());	        	 
	        	 }   
	         }
    		 ecb_currency_rates.put("EUR", "1");
    		 ecb_currency_names_requests.put("EUR", 0);
	         
	         
		 
		 } catch (Exception e) {
			 
			 e.printStackTrace();
			 
		 }
		
	}

}
