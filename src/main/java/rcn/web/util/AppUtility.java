package rcn.web.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:rol_cable_network.properties")
public class AppUtility {

	@Value("${fast2sms.api.url}")
	private String apiURL;

	@Value("${fast2sms.api.key}")
	private String apiKey;

	@Value("${fast2sms.api.request.contentType}")
	private String contentType;

	public long getDifferenceDays(Date d1, Date d2) {
		long diff = d2.getTime() - d1.getTime();
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public Date getTodaysDateWithoutTime() {
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			return formatter.parse(formatter.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Date formatDate(Date date) {
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			return formatter.parse(formatter.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Date formatStringToDate(String stateChangeDate) {
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			return formatter.parse(stateChangeDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Date getOneMonthAheadDate(Date currentDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.DATE, 30);
		return c.getTime();
	}

	public String sendSMS(String phone, String message) throws Exception {

		System.out.println("Sending message to - " + phone);

		String requestBody = new StringBuilder("sender_id=")
				.append("FTWSMS")
				.append("&message=")
				.append(message)
				.append("&route=dlt&numbers=")
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

		System.out.println("Received Response Code for Fast2SMS Server :: " + con.getResponseMessage());

		if (responseCode == HttpURLConnection.HTTP_OK) {
			return "Success";
		} else {
			System.out.println("OTP request did not work!");
			return "Request Failed";
		}

	}

}
