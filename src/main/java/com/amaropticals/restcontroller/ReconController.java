package com.amaropticals.restcontroller;

import com.amaropticals.common.CacheMap;
import com.amaropticals.common.CommonUtils;
import com.amaropticals.common.MailUtils;
import com.amaropticals.dao.GenericDAO;
import com.amaropticals.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/recon")
@RestController
public class ReconController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private GenericDAO stocksDAO;

	@Autowired
	private StockController stockController;

	@Autowired
	private TaskController taskController;

	@Value("${amaropticals.invoices.path}")
	private String invoicePath;

	@Value("${amaropticals.xsl.path.invoice}")
	private String invoiceXsl;

	@Value("${amaropticals.reports.email}")
	private String emailAddresses;

	@RequestMapping(value = "/addReconRecord", method = RequestMethod.POST)
	public ResponseEntity<AddReconResponse> addReconRecord(HttpServletRequest request,
			@RequestBody AddReconRequest reconRequest) {

		AddReconResponse response = new AddReconResponse();
		if (!CommonUtils.checkAuthentication(request)) {
			response.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<AddReconResponse>(response, HttpStatus.OK);

		}

		// compare data display qty + recon qty is equal to stock qty, if not send email
		 List<ReconModel> reconModelList = new ArrayList<>();
		 	 reconRequest.getReconModelList().stream().forEach( reconModel ->
		 {
			 int totalQty = reconModel.getDisplayQty() + reconModel.getReconQty();

			 String sql = "SELECT * from opticals_stocks WHERE product_code LIKE " + " '" + reconModel.getProductCode() + "';";

			 List<StockModel> stockModels = stocksDAO.findStocks(sql);
			 reconModel.setDiff(totalQty-stockModels.get(0).getQuantity());
			 reconModel.setReconDateTime(Timestamp.valueOf(LocalDateTime.now()).toString());
			 reconModelList.add(reconModel);

		 });
		UserModel userModel = (UserModel) CacheMap.readEntry(request.getHeader("token"));


		//store into recon table
		for (ReconModel reconModel : reconModelList) {
			String sql = "INSERT INTO opticals_stocks_recon (product_id, product_code, "
					+ " recon_qty, user, diff) VALUES (?, ?, ?, ?,?)";
			stocksDAO.addOrUpdateInvoice(sql, reconModel.getProductId(), reconModel.getProductCode(),
					reconModel.getReconQty(), userModel.getName(), reconModel.getDiff());


		}
		reconRequest.setReconModelList(reconModelList);
		MailUtils.sendMail(emailAddresses,
				"MIS-MATCHED LIST DATE/TIME :"+ new Date(),
				reconModelList.stream().filter(reconModel -> reconModel.getDiff() != 0).collect(Collectors.toList()));
		MailUtils.sendMail(emailAddresses,
				"MATCHED LIST DATE/TIME :"+ new Date(),
				reconModelList.stream().filter(reconModel -> reconModel.getDiff() == 0).collect(Collectors.toList()));

		response.setResponse(reconRequest);
		return new ResponseEntity<AddReconResponse>(response, HttpStatus.OK);
	}







}
