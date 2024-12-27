package com.amaropticals.common;

import java.io.IOException;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.transform.TransformerException;

import com.amaropticals.model.*;
import org.apache.fop.apps.FOPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.istack.ByteArrayDataSource;

public class MailUtils {
	private final static Logger LOGGER = LoggerFactory.getLogger(MailUtils.class);

	private static final String host = "host12.dns2dns.com";
	private static final String user = "admin@amaropticals.in";// change accordingly
	private static final String password = "$Rajeshnisha8$";// change accordingly

	private static Properties props = null;
	private static Map<String, String> monthMap = null;

	static {
		monthMap = new HashMap<String, String>();
		monthMap.put("01", "Jan");
		monthMap.put("02", "Feb");
		monthMap.put("03", "Mar");
		monthMap.put("04", "Apr");
		monthMap.put("05", "May");
		monthMap.put("06", "Jun");
		monthMap.put("07", "Jul");
		monthMap.put("08", "Aug");
		monthMap.put("09", "Sep");
		monthMap.put("10", "Oct");
		monthMap.put("11", "Nov");
		monthMap.put("12", "Dec");

	}

	private static void loadProperties() {

		// Get the session object
		if (props == null) {
			props = new Properties();
			props.put("mail.smtp.host", host);
			// props.put("mail.smtp.socketFactory.port", "465"); // SSL Port
			// props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			// // SSL Factory Class
			props.put("mail.smtp.auth", "true"); // Enabling SMTP Authentication
			props.put("mail.smtp.port", "587"); // SMTP Port
		}

	}

	private static Session getSession() {
		loadProperties();
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});
		return session;

	}

	public static void sendMail(String toAddress, String subject, Object... request) {

		try {
			MimeMessage message = new MimeMessage(getSession());

			Multipart multipart = new MimeMultipart();

			if (request[0] instanceof CreateInvoiceRequest) {
				CreateInvoiceRequest createInvoiceRequest = (CreateInvoiceRequest) request[0];
				message.setFrom(new InternetAddress(user));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
				message.setSubject(subject);

				StringBuilder messageBody = new StringBuilder();
				messageBody.append("Hello " + createInvoiceRequest.getName() + ",");
				messageBody.append(
						"\nPlease find the attached receipt of your purchase.Do provide us the opportunity to serve you again.\n\nWarm Regards,\nAmar Opticals,\nNear Pushpak Electronics,\nT.P.Nagar, Korba\nPh:7067979795");

				BodyPart messageBodyPart1 = new MimeBodyPart();
				messageBodyPart1.setText(messageBody.toString());

				// 4) create new MimeBodyPart object and set DataHandler object to this object

				// 5) create Multipart object and add MimeBodyPart objects to this object

				multipart.addBodyPart(messageBodyPart1);
				multipart.addBodyPart(createPdfAttachment(createInvoiceRequest, (String)request[2]));
			} else if (request[0] instanceof ReportModel) {

				ReportModel reportModel = (ReportModel) request[0];
				message.setFrom(new InternetAddress(user));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
				message.setSubject("Sales summary :"+monthMap.get(reportModel.getMonth())+" "+reportModel.getYear());

				StringBuilder messageBody = new StringBuilder();
				messageBody.append("Hello,");
				messageBody.append("\nPlease find Report for the month of "+monthMap.get(reportModel.getMonth())+" "+reportModel.getYear());
				messageBody.append("\n Total Sales Amount :"+ (String)request[1]);
				messageBody.append("\n Total Customers :"+ (String)request[2]);
				messageBody.append("\n Average Sales per customer :"+ (String)request[3]);
				
				BodyPart messageBodyPart1 = new MimeBodyPart();
				messageBodyPart1.setText(messageBody.toString());

				// 4) create new MimeBodyPart object and set DataHandler object to this object

				// 5) create Multipart object and add MimeBodyPart objects to this object

				multipart.addBodyPart(messageBodyPart1);

			} else if (request[0] instanceof TransactionModel) {

				TransactionModel trxnModel = (TransactionModel) request[0];
				message.setFrom(new InternetAddress(user));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
				message.setSubject(subject);
				StringBuilder messageBody = new StringBuilder();
				messageBody.append("Hello,");
				messageBody.append("This is transaction details:");
				messageBody.append("TransactionId:"+ trxnModel.getTransactionId());
				messageBody.append("TransactionType:"+ trxnModel.getTransactionType());
				messageBody.append("TotalAmount:"+ trxnModel.getTotalAmount());
				messageBody.append("BalanceAmount:"+ trxnModel.getBalanceAmount());
				messageBody.append("DeliveryDate:"+ trxnModel.getDeliveryDate());

				BodyPart messageBodyPart1 = new MimeBodyPart();
				messageBodyPart1.setText(messageBody.toString());

				// 4) create new MimeBodyPart object and set DataHandler object to this object

				// 5) create Multipart object and add MimeBodyPart objects to this object

				multipart.addBodyPart(messageBodyPart1);

			} else if(request[0] instanceof QueryModel) {

				QueryModel queryModel = (QueryModel) request[0];
				message.setFrom(new InternetAddress(user));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
				message.setSubject(subject);
				StringBuilder messageBody = new StringBuilder();
				messageBody.append("\nQuery Id:"+ queryModel.getQueryId());
				messageBody.append("\nwith reason:"+ queryModel.getReason());
				messageBody.append("\nfor person :"+ queryModel.getName());
				messageBody.append("\nentered by:"+ queryModel.getCreatedBy());
				messageBody.append("\n time :"+ queryModel.getUpdateTime());

				BodyPart messageBodyPart1 = new MimeBodyPart();
				messageBodyPart1.setText(messageBody.toString());

				// 4) create new MimeBodyPart object and set DataHandler object to this object

				// 5) create Multipart object and add MimeBodyPart objects to this object

				multipart.addBodyPart(messageBodyPart1);

			}else if(request[0] instanceof List && ((List)request[0]).get(0) instanceof ReconModel) {

				List<ReconModel> reconModelList = (List<ReconModel>) request[0];
				message.setFrom(new InternetAddress(user));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
				message.setSubject(subject);
				StringBuilder messageBody = new StringBuilder();
				messageBody.append("LIST OF RECORDS\n ");
						for (ReconModel reconModel:reconModelList) {
							messageBody.append("<b>product code:</b>" + reconModel.getProductCode());
							messageBody.append("\n");
							messageBody.append("<b>recon qty:</b>" + reconModel.getReconQty());
							messageBody.append("\n");
							messageBody.append("<b>diff qty:</b>" + reconModel.getDiff());
							messageBody.append("\n");
							messageBody.append("<b>recon time:</b>" + reconModel.getReconDateTime());
							messageBody.append("\n");
						}

				messageBody.append("diff qty in -ve = total qty is less than recon qty\n");
				messageBody.append("diff qty in +ve = total qty is greater than recon qty\n");
				BodyPart messageBodyPart1 = new MimeBodyPart();
				messageBodyPart1.setText(messageBody.toString());

				// 4) create new MimeBodyPart object and set DataHandler object to this object

				// 5) create Multipart object and add MimeBodyPart objects to this object

				multipart.addBodyPart(messageBodyPart1);

			}
			// 6) set the multiplart object to the message object
			message.setContent(multipart);

			// send the message
			Transport.send(message);

			LOGGER.info("Mail sent successfully to={}", toAddress);

		} catch (Exception e) {
			LOGGER.error("Can't send mail to={}, due to exception={}", toAddress, e);

		}
	}

	private static MimeBodyPart createPdfAttachment(CreateInvoiceRequest createInvoiceRequest, String invoiceXsl)
			throws MessagingException, FOPException, IOException, TransformerException {

		String filename = createInvoiceRequest.getInvoiceId() + ".pdf";// change accordingly
		PDFUtils pdfUtils = new PDFUtils();
		DataSource source = null;

		source = new ByteArrayDataSource(pdfUtils.convertToPDF(createInvoiceRequest,invoiceXsl), "application/pdf");
		MimeBodyPart messageBodyPart2 = new MimeBodyPart();
		messageBodyPart2.setDataHandler(new DataHandler(source));
		messageBodyPart2.setFileName(filename);

		return messageBodyPart2;
	}

}
