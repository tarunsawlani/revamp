package com.amaropticals.common;

import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amaropticals.model.CreateInvoiceRequest;
import com.amaropticals.model.UserModel;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

public class CommonUtils {

	private final static Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);
	private static long invoiceId;
	private static long queryId;

	private static long ONE_HOUR_MILLISECONDS = 3600000;

	public static long getNextInvoiceId() {
		long nextInvoiceId;

		Calendar cal = Calendar.getInstance();

		StringBuilder date = new StringBuilder();
		date.append(String.valueOf(cal.get(1)).substring(2, 4));
		if (cal.get(2) + 1 < 10) {
			date.append(0);
		}
		date.append(cal.get(2) + 1);
		if (cal.get(5) < 10) {
			date.append(0);
		}
		date.append(cal.get(5));

		if (Long.parseLong(date.toString() + "00") > invoiceId) {
			nextInvoiceId = Long.parseLong(date.toString() + "01");
		} else {

			nextInvoiceId = invoiceId + 1;
		}

		invoiceId = nextInvoiceId;

		LOGGER.info("Next Invoice Id={}", nextInvoiceId);
		return nextInvoiceId;
	}

	public static long getNextQueryId() {
		long nextQueryId;

		Calendar cal = Calendar.getInstance();

		StringBuilder date = new StringBuilder();
		date.append(String.valueOf(cal.get(1)).substring(2, 4));
		if (cal.get(2) + 1 < 10) {
			date.append(0);
		}
		date.append(cal.get(2) + 1);
		if (cal.get(5) < 10) {
			date.append(0);
		}
		date.append(cal.get(5));

		if (Long.parseLong(date.toString() + "00") > queryId) {
			nextQueryId = Long.parseLong(date.toString() + "01");
		} else {

			nextQueryId = queryId + 1;
		}

		queryId = nextQueryId;

		LOGGER.info("Next Query Id={}", nextQueryId);
		return nextQueryId;
	}

	public static boolean sendMessages(CreateInvoiceRequest request, String status) {

		boolean sent = false;
		try {

			DefaultClientConfig defaultClientConfig = new DefaultClientConfig();
			SSLContext sslContext = SSLContext.getInstance("SSL");

			ServerTrustManager manager = new ServerTrustManager();

			sslContext.init(null, new TrustManager[] { manager }, null);
			defaultClientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
					new HTTPSProperties(null, sslContext));
			Client client = Client.create(defaultClientConfig);

			WebResource webResource = client.resource("https://www.fast2sms.com/dev/bulk");

			ClientResponse response = webResource
					.header("authorization",
							"4cuqXLQCY3ePfnk7tpz5vsG2DoNiBMhFI0RdZKrwSA98bxOWyE4mZQ1iXFxpAY6wDKegnzjIUyW8J9st")
					.header("cache-control", "no-cache").header("content-type", "application/x-www-form-urlencoded")
					.type("application/x-www-form-urlencoded").post(ClientResponse.class, getMessage(request, status));

			if (response.getStatus() == 200 || response.getStatus() == 201) {
				sent = true;
			} else {

				String output = response.getEntity(String.class);

				LOGGER.info("Message  Sent Failed:" + output);

			}

		} catch (Exception e) {

			e.printStackTrace();

		}
		// return response.getStatusText();
		return sent;
	}

	private static String getMessage(CreateInvoiceRequest request, String status) {

		String message = "sender_id=AMROPT&message=1136&language=english&route=qt&numbers=" + request.getContact()
				+ "&variables={#CC#}|{#BB#}|{#EE#}|{#DD#}&variables_values=" + request.getName() + "|"
				+ request.getInvoiceId() + "|IN PROGRESS|" + request.getDeliveryDate() + "&flash=0";

		if ("READY FOR PICKUP".equalsIgnoreCase(status)) {

			message = "sender_id=AMROPT&message=1137&language=english&route=qt&numbers=" + request.getContact()
					+ "&variables={#CC#}|{#BB#}|{#EE#}&variables_values= " + request.getName() + "|"
					+ request.getInvoiceId() + "|READY FOR PICKUP&flash=0";
		}

		return message;

	}

	public static long getInvoiceId() {
		return invoiceId;
	}

	public static synchronized void setInvoiceId(long invoiceId) {
		CommonUtils.invoiceId = invoiceId;
	}

	public static long getQueryId() {
		return queryId;
	}

	public static synchronized void setQueryId(long queryId) {
		CommonUtils.queryId = queryId;
	}

	public static boolean checkAuthentication(HttpServletRequest request) {
		boolean result = false;
		UserModel userModel = (UserModel) CacheMap.readEntry(request.getHeader("token"));
		if (userModel != null) {

			result = (System.currentTimeMillis() - userModel.getLoginTime().getTime()) < 24 * ONE_HOUR_MILLISECONDS;
		}

		return result;

	}
}
