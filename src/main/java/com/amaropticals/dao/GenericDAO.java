package com.amaropticals.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.amaropticals.common.CommonUtils;
import com.amaropticals.daomapper.CustomerModelMapper;
import com.amaropticals.daomapper.InvoiceModelMapper;
import com.amaropticals.daomapper.StockModelMapper;
import com.amaropticals.daomapper.TaskModelMapper;
import com.amaropticals.daomapper.UserModelMapper;
import com.amaropticals.model.CreateInvoiceRequest;
import com.amaropticals.model.CustomerModel;
import com.amaropticals.model.StockModel;
import com.amaropticals.model.TaskModel;
import com.amaropticals.model.UserModel;

@Transactional
@Repository
public class GenericDAO {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private final JdbcTemplate genericJdbcTemplate;

	@Autowired
	public GenericDAO(JdbcTemplate jdbcTemplate) {
		this.genericJdbcTemplate = jdbcTemplate;
	}

	public List<StockModel> findStocks(String sql) {

		List<StockModel> list = genericJdbcTemplate.query(sql, new StockModelMapper());
		return list;

	}

	public List<TaskModel> findTasks(String sql) {

		List<TaskModel> list = genericJdbcTemplate.query(sql, new TaskModelMapper());
		return list;

	}
	
	public List<UserModel> findUsers(String sql) {

		List<UserModel> list = genericJdbcTemplate.query(sql, new UserModelMapper());
		return list;

	}
	
	public List<CreateInvoiceRequest> findInvoices(String sql) {

		List<CreateInvoiceRequest> list = genericJdbcTemplate.query(sql, new InvoiceModelMapper());
		return list;

	}
	
	public List<CustomerModel> findCustomer(String sql) {

		List<CustomerModel> list = genericJdbcTemplate.query(sql, new CustomerModelMapper());
		return list;

	}
	
	public List<String> query(String sql) {

		List<String> list = genericJdbcTemplate.queryForList(sql,  String.class);
		return list;

	}

	public String addOrUpdateStocks(String sql, Object... model) {

		int row = genericJdbcTemplate.update(sql, model);

		if (row > 0) {

			return "success";
		}

		return null;
		// return list;
	}

	public String addOrUpdateInvoice(String sql, Object... model) {

		int row = genericJdbcTemplate.update(sql, model);

		if (row > 0) {

			return "success";
		}

		return null;
		// return list;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void loadMaxInvoiceIdfromDB() {

		String sql = "SELECT MAX(invoice_id) FROM opticals_invoices;";
		Long invoiceId = genericJdbcTemplate.queryForObject(sql, Long.class);
		LOGGER.info("Loaded Max Invoice Id={} from DB", invoiceId);
		CommonUtils.setInvoiceId(invoiceId);
	}
	
	public SqlRowSet genericQuery(String sql) {

		SqlRowSet rowSet  = genericJdbcTemplate.queryForRowSet(sql);
		return rowSet;

	}

}
