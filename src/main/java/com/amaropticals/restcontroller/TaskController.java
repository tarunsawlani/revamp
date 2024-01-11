package com.amaropticals.restcontroller;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.amaropticals.common.MailUtils;
import com.amaropticals.model.*;
import org.apache.commons.lang3.StringUtils;
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

import com.amaropticals.common.AOConstants;
import com.amaropticals.common.CommonUtils;
import com.amaropticals.dao.GenericDAO;

@RequestMapping("/tasks")
@RestController
public class TaskController {

	@Autowired
	private GenericDAO stocksDAO;

	@Autowired
	private InvoiceController invoiceController;

	private final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

	@Value("${amaropticals.reports.email}")
	private String emailAddresses;

	@RequestMapping(value = "/createTasks", method = RequestMethod.POST)
	public CommonResponse createTasks(@RequestBody TaskModel model) {
		CommonResponse response = new CommonResponse();
		
		LOGGER.info("Adding Tasking taskId={}, taskStatus={}", model.getTaskId(), model.getTaskStatus());

		String sql = "INSERT INTO opticals_tasks (task_id, task_status, name, delivery_date, update_timestamp, user, balance_amount)"
				+ " VALUES(?,?,?,?,?,?, ?);";
		stocksDAO.addOrUpdateInvoice(sql, model.getTaskId(), model.getTaskStatus(), model.getName(), model.getDeliveryDate(),
				model.getUpdateTime(), model.getUser(), Double.parseDouble(model.getBalanceAmount()));
		
		response.setStatus(AOConstants.SUCCESS_TEXT);
		return response;

	}

	@RequestMapping(value = "/searchTasks", method = RequestMethod.POST)
	public ResponseEntity<TaskModelList> searchTasks(HttpServletRequest request, @RequestBody TaskModel model) {
		TaskModelList response = new TaskModelList();
		if (!CommonUtils.checkAuthentication(request)) {
			response.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<TaskModelList>(response, HttpStatus.OK);

		}
		List<TaskModel> taskModelList = searchTaskGen(model);
		response.setTaskModelList(taskModelList);
		return new ResponseEntity<TaskModelList>(response, HttpStatus.OK);
	}

	private List<TaskModel> searchTaskGen(TaskModel model) {
		LOGGER.info("Search Tasking taskId={}, taskStatus={}", model.getTaskId(), model.getTaskStatus());

		String sql = "SELECT * from opticals_tasks;";

		if (StringUtils.isNotBlank(model.getTaskId())) {
			sql = "SELECT * from opticals_tasks WHERE task_id LIKE  '" + model.getTaskId() + "%';";
		} else if (StringUtils.isNotBlank(model.getTaskStatus())) {
			sql = "SELECT * from opticals_tasks WHERE task_status LIKE '" + model.getTaskStatus() + "';";
		} else if (StringUtils.isNotBlank(model.getDeliveryDate())) {

			sql = "SELECT * from opticals_tasks WHERE delivery_date > '" + Date.valueOf(model.getDeliveryDate()) + "';";

		} else {

			sql = "SELECT * from opticals_tasks ;";
		}
	List<TaskModel>	taskModelList = stocksDAO.findTasks(sql);
		return taskModelList;
	}

	@RequestMapping(value = "/updateTasks", method = RequestMethod.POST)
	public ResponseEntity<CommonResponse> updateTasks(HttpServletRequest request,@RequestBody TaskModel model) {
		LOGGER.info("Updating Tasking taskId={}, taskStatus={}", model.getTaskId(), model.getTaskStatus());
		CommonResponse response = new CommonResponse();
		if (!CommonUtils.checkAuthentication(request)) {
			response.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);

		}
		TaskModel oldModel = searchTaskGen(model).get(0);

		model.setTaskId(oldModel.getTaskId());
		model.setDeliveryDate(model.getDeliveryDate().substring(0,10));
		String sql = "UPDATE  opticals_tasks SET  task_status=? , delivery_date=?, update_timestamp=?, user=?, update_user=?, payment_mode=?, " +
				"balance_amount=?, comments=? WHERE task_id=?;";
		stocksDAO.addOrUpdateInvoice(sql, model.getTaskStatus(), model.getDeliveryDate(),
				Timestamp.valueOf(LocalDateTime.now()), model.getUser(), model.getUpdatedBy(), model.getPaymentMode(),
				Double.parseDouble(model.getBalanceAmount()), model.getComments(),model.getTaskId());
		if ("READY FOR PICKUP".equalsIgnoreCase(model.getTaskStatus())) {
			CommonUtils.sendMessages(invoiceController.getModelInvoice(Long.valueOf(model.getTaskId().split("-")[0])),
					model.getTaskStatus());


		}

		if ("DELIVERED".equalsIgnoreCase(model.getTaskStatus())) {
			TransactionModel transactionModel = new TransactionModel();
			transactionModel.setTransactionType("UPD_TASK");
			transactionModel.setTransactionId(model.getTaskId());
			transactionModel.setTotalAmount(null);
			transactionModel.setBalanceAmount(model.getBalanceAmount());
			transactionModel.setDeliveryDate(model.getDeliveryDate());
			MailUtils.sendMail(emailAddresses,

					transactionModel.getTransactionType()
							+ "-TaskId:" + transactionModel.getTransactionId() +
							"-BAmt:" + transactionModel.getBalanceAmount() +
							"Mode" + model.getPaymentMode(),
					transactionModel);
		}
		response.setStatus(AOConstants.SUCCESS_TEXT);
		return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
	}
}
