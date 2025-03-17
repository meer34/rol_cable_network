package rcn.web.util;

import java.io.OutputStream;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@PropertySource("classpath:rol_cable_network.properties")
@Slf4j
public class AppUtility {

	@Value("${fast2sms.api.url}")
	private String apiURL;

	@Value("${fast2sms.api.key}")
	private String apiKey;

	@Value("${fast2sms.api.request.contentType}")
	private String contentType;
	
	@Value("${RENEWAL_CYCLE}")
	private int renewalCycle;

	public long getDifferenceDays(Date d1, Date d2) {
		long diff = d2.getTime() - d1.getTime();
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
	}

	public Date getTodaysDateWithoutTime() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.parse(formatter.format(new Date()));
	}
	
	public Date getTomorrowsDateWithoutTime() throws ParseException {
		Date date = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(date); 
		c.add(Calendar.DATE, 1);
		date = c.getTime();

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.parse(formatter.format(date));
	}

	public Date formatDate(Date date) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.parse(formatter.format(date));
	}

	public Date formatStringToDate(String stateChangeDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.parse(stateChangeDate);
	}

	public Date getOneDayAheadDate(Date currentDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.DATE, 1);
		return c.getTime();
	}
	
	public Date getOneMonthAheadDate(Date currentDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.DATE, renewalCycle - 1);
		return c.getTime();
	}

	public String sendSMS(String phone, String message) throws Exception {

		log.info("Sending message to - " + phone);

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

		log.info("Received Response Code for Fast2SMS Server :: " + con.getResponseMessage());

		if (responseCode == HttpURLConnection.HTTP_OK) {
			return "Success";
		} else {
			log.info("OTP request did not work!");
			return "Request Failed";
		}

	}

	public double roundDecimal(double doubleValue) {
		DecimalFormat df_obj = new DecimalFormat("#.##");
		df_obj.setRoundingMode(RoundingMode.UP);
		return Double.valueOf(df_obj.format(doubleValue));
	}

}
