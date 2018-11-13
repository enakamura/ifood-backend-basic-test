package com.ifood.config;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class Helper {

	public static MultiValueMap<String, String> formatHttpHeader(String uuid) {
		MultiValueMap<String, String> headerParam = new LinkedMultiValueMap<String, String>();
		headerParam.add("Content-Type", "application/json;charset=utf-8");
		headerParam.add("MessageId", uuid);
		return headerParam; 
	}
	
	public static String formatMessage(String message) {
		return message.replace("{", "").replace("}", "").replace("\"", "");
	}
}
