package com.amaropticals.restcontroller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;

import com.amaropticals.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.fop.apps.FOPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amaropticals.common.AOConstants;
import com.amaropticals.common.CommonUtils;
import com.amaropticals.common.MailUtils;
import com.amaropticals.common.PDFUtils;
import com.amaropticals.dao.GenericDAO;
import com.amaropticals.filehandling.JSONFileHandler;

@RequestMapping("/invoices")
@RestController
public class InvoiceController {

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

	@RequestMapping(value = "/createInvoice", method = RequestMethod.POST)
	public ResponseEntity<CreateInvoiceResponse> createInvoice(HttpServletRequest request,
			@RequestBody CreateInvoiceRequest invoiceRequest) {

		CreateInvoiceResponse response = new CreateInvoiceResponse();
		if (!CommonUtils.checkAuthentication(request)) {
			response.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<CreateInvoiceResponse>(response, HttpStatus.OK);

		}
		invoiceRequest.setInvoiceId(CommonUtils.getNextInvoiceId());
		invoiceRequest.setJsonFileName(invoiceRequest.getInvoiceId() + ".json");

		invoiceRequest.setUpdateDate(Timestamp.valueOf(LocalDateTime.now()).toString());

		if (StringUtils.isBlank(invoiceRequest.getDeliveryDate())) {
			invoiceRequest.setDeliveryDate(invoiceRequest.getUpdateDate().substring(0, 10));

		} else {
			invoiceRequest.setDeliveryDate(invoiceRequest.getDeliveryDate().substring(0, 10));

		}

		String sql = "INSERT INTO opticals_invoices (invoice_id, name , email , contact, delivery_date, total_amount,"
				+ " initial_amount, update_timestamp, json_file_name) VALUES (?, ?, ?, ?, ?,?,?,?,?)";
		stocksDAO.addOrUpdateInvoice(sql, invoiceRequest.getInvoiceId(), invoiceRequest.getName(),
				invoiceRequest.getEmail(), invoiceRequest.getContact(), Date.valueOf(invoiceRequest.getDeliveryDate()),
				invoiceRequest.getTotalAmount(), invoiceRequest.getInitialAmount(), invoiceRequest.getUpdateDate(),
				invoiceRequest.getInvoiceId() + ".json");
		JSONFileHandler.writeJsonFile(invoicePath, String.valueOf(invoiceRequest.getInvoiceId()).substring(0, 4),
				invoiceRequest.getJsonFileName(), invoiceRequest);
		if (!invoiceRequest.isWithoutDetailsInvoice()) {
			checkAndPopulateTasksAndDate(invoiceRequest);
		}
		updateStocks(invoiceRequest);

		if (StringUtils.isNotBlank(invoiceRequest.getEmail())) {
			MailUtils.sendMail(invoiceRequest.getEmail(),
					"Your purchase at Amar Opticals Invoice Id:" + invoiceRequest.getInvoiceId(),
					invoiceRequest, invoiceXsl);
		}

		TransactionModel transactionModel = new TransactionModel();
		transactionModel.setTransactionType("CREATE_INV");
		transactionModel.setTransactionId(Long.toString(invoiceRequest.getInvoiceId()));
		transactionModel.setTotalAmount(invoiceRequest.getTotalAmount());
		transactionModel.setBalanceAmount(invoiceRequest.getPendingAmount());
		transactionModel.setDeliveryDate(invoiceRequest.getDeliveryDate());
		MailUtils.sendMail(emailAddresses,
				 transactionModel.getTransactionType()
				+ "Txn-Id:"+ transactionModel.getTransactionId()+
						"TAmt:"+transactionModel.getTotalAmount() +
						 "BAmt:" +transactionModel.getBalanceAmount()+
						 "PM:"+invoiceRequest.getPaymentMode(),
				transactionModel);
		response.setResponse(invoiceRequest);
		return new ResponseEntity<CreateInvoiceResponse>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getInvoicesByMonth/{yymm}", method = RequestMethod.GET)
	public List<CreateInvoiceRequest> getInvoicesByMonth(HttpServletRequest request, @PathVariable("yymm") long yymm) {

		String startInvoice = yymm + "0100";
		String endInvoice = yymm + "3200";

		String sql = "SELECT * FROM opticals_invoices WHERE invoice_id > " + Long.valueOf(startInvoice)
				+ " AND invoice_id < " + Long.valueOf(endInvoice) + ";";

		return stocksDAO.findInvoices(sql);
	}

	@RequestMapping(value = "/getInvoicesByYear/{yy}", method = RequestMethod.GET)
	public ResponseEntity<InvoiceListResponse> getInvoicesByYear(HttpServletRequest request,
			@PathVariable("yy") long yy) {
		InvoiceListResponse response = new InvoiceListResponse();
		if (!CommonUtils.checkAuthentication(request)) {
			response.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<InvoiceListResponse>(response, HttpStatus.OK);

		}
		String startInvoice = yy + "010100";
		String endInvoice = yy + "123200";

		String sql = "SELECT * FROM opticals_invoices WHERE invoice_id > " + Long.valueOf(startInvoice)
				+ " AND invoice_id < " + Long.valueOf(endInvoice) + ";";

		response.setInvoiceList(stocksDAO.findInvoices(sql));
		return new ResponseEntity<InvoiceListResponse>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getInvoice/{invoiceId}", method = RequestMethod.GET)
	public ResponseEntity<CreateInvoiceRequest> getInvoice(HttpServletRequest request,
			@PathVariable("invoiceId") long invoiceId) {
		CreateInvoiceRequest model = new CreateInvoiceRequest();

		if (!CommonUtils.checkAuthentication(request)) {
			model.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<CreateInvoiceRequest>(model, HttpStatus.OK);

		}

		return new ResponseEntity<CreateInvoiceRequest>(getModelInvoice(invoiceId), HttpStatus.OK);
	}

	@RequestMapping(value = "/downloadInvoice/{invoiceId}", method = RequestMethod.GET, produces = "application/pdf")
	public InputStreamResource downloadInvoice(HttpServletRequest request,
			@PathVariable("invoiceId") long invoiceId) throws FOPException, IOException, TransformerException {
		
		CreateInvoiceRequest model = getModelInvoice(invoiceId);
		LOGGER.info("Read model from json file, invoiceId={}", model.getInvoiceId());
		PDFUtils utils = new PDFUtils();
		byte[] out = utils.convertToPDF(model, invoiceXsl);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/pdf"));
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
		headers.add("Access-Control-Allow-Headers", "Content-Type");
		headers.add("Content-Disposition", "attachment;filename=" + String.valueOf(invoiceId) + ".pdf");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");

		headers.setContentLength(out.length);

		ByteArrayInputStream aa = new ByteArrayInputStream(out);
		InputStreamResource response = new InputStreamResource(aa);
		return response;
	}

	private void checkAndPopulateTasksAndDate(CreateInvoiceRequest request) {

		LOGGER.info("Checking for lens purchase for invoiceId={}", request.getInvoiceId());
		int count = 1;

		List<ItemModel> lensList = request.getPurchaseItems().stream().filter(item -> true == item.isLensActive())
				.collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(lensList)) {
			for (ItemModel model : lensList) {

				model.setTaskId(request.getInvoiceId() + "-" + count);
				model.setDeliveryDate(request.getDeliveryDate());
				TaskModel taskModel = new TaskModel();
				taskModel.setName(request.getName());
				taskModel.setTaskId(model.getTaskId());
				taskModel.setTaskStatus(AOConstants.TASK_IN_PROGRESS);
				taskModel.setDeliveryDate(model.getDeliveryDate());
				taskModel.setUpdateTime(request.getUpdateDate());
				taskModel.setBalanceAmount(request.getPendingAmount());
				taskController.createTasks(taskModel);

				count++;
				LOGGER.info("Lens task identifed for lens purchase for invoiceId={}, task={}", request.getInvoiceId(),
						model.getTaskId());

			}
			// CommonUtils.sendMessages(request, "");
		}

	}

	@Async
	private void updateStocks(CreateInvoiceRequest request) {
		LOGGER.info("Updating stocks for invoiceId={}", request.getInvoiceId());

		for (ItemModel item : request.getPurchaseItems()) {
			if (!item.isLensActive()) {
				AddOrUpdateStockRequest model = new AddOrUpdateStockRequest();
				model.setProductId(item.getProductId());
				model.setQuantityChange(-item.getBuyQuantity());
				model.setReason("Invoice");
				model.setRefId(String.valueOf(request.getInvoiceId()));
				model.setUpdateDate(request.getUpdateDate());
				model.setUser("user");
				stockController.updateStocks(model);
			}

		}

	}

	protected CreateInvoiceRequest getModelInvoice(long invoiceId) {
		CreateInvoiceRequest model = (CreateInvoiceRequest) JSONFileHandler.readJsonFile(invoicePath,
				String.valueOf(invoiceId).substring(0, 4), String.valueOf(invoiceId) + ".json",
				CreateInvoiceRequest.class);

		return model;
	}
}
