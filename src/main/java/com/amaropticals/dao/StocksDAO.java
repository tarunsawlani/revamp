package com.amaropticals.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.amaropticals.common.CommonUtils;
import com.amaropticals.daomapper.StockModelMapper;
import com.amaropticals.daomapper.TaskModelMapper;
import com.amaropticals.model.StockModel;
import com.amaropticals.model.TaskModel;

@Transactional
@Repository
public class StocksDAO {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public StocksDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<StockModel> findStocks(String sql) {

		List<StockModel> list = jdbcTemplate.query(sql, new StockModelMapper());
		return list;

	}

	public List<TaskModel> findTasks(String sql) {

		List<TaskModel> list = jdbcTemplate.query(sql, new TaskModelMapper());
		return list;

	}
	
	public List<String> query(String sql) {

		List<String> list = jdbcTemplate.queryForList(sql,  String.class);
		return list;

	}

	public String addOrUpdateStocks(String sql, Object... model) {

		int row = jdbcTemplate.update(sql, model);

		if (row > 0) {

			return "success";
		}

		return null;
		// return list;
	}

	public String addOrUpdateInvoice(String sql, Object... model) {

		int row = jdbcTemplate.update(sql, model);

		if (row > 0) {

			return "success";
		}

		return null;
		// return list;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void loadMaxInvoiceIdfromDB() {

		String sql = "SELECT MAX(invoice_id) FROM opticals_invoices;";
		Long invoiceId = jdbcTemplate.queryForObject(sql, Long.class);
		LOGGER.info("Loaded Max Invoice Id={} from DB", invoiceId);
		CommonUtils.setInvoiceId(invoiceId);
	}

}
