package com.hunter.web.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:hunter_garments.properties")
public class AppUtility {
	
	@Value("${fast2sms.api.url}")
	private String apiURL;

	@Value("${fast2sms.api.key}")
	private String apiKey;

	@Value("${fast2sms.api.request.contentType}")
	private String contentType;
	
	public String sendSMS(String phone, String message) throws Exception {
		
		System.out.println("Sending message to - " + phone);
		
		String requestBody = new StringBuilder("sender_id=")
				.append("FTWSMS")
				.append("&message=")
				.append(message)
				.append("&route=v3&numbers=")
				.append(phone)
				.append("&flash=")
				.append("0")
				.toString();

		URL urlObj = new URL(apiURL);
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("authorization", apiKey);
		con.setRequestProperty("Content-Type", contentType);
		con.setDoOutput(true);

		try(OutputStream os = con.getOutputStream()) {
			byte[] input = requestBody.getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		int responseCode = con.getResponseCode();

		System.out.println("Received Response Code for Fast2SMS Server :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) {
			return "Success";
		} else {
			System.out.println("OTP request did not work!");
			return "Request Failed";
		}

	}

}
