package com.amaropticals.restcontroller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amaropticals.common.AOConstants;
import com.amaropticals.common.CommonUtils;
import com.amaropticals.dao.GenericDAO;
import com.amaropticals.filehandling.JSONFileHandler;
import com.amaropticals.model.AOError;
import com.amaropticals.model.AddOrUpdateStockRequest;
import com.amaropticals.model.AddOrUpdateStockResponse;
import com.amaropticals.model.StockLogModel;
import com.amaropticals.model.StockModel;
import com.amaropticals.model.StockModelListResponse;

@RequestMapping("/stocks")
@RestController
public class StockController {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private GenericDAO stocksDAO;

	@Value("${amaropticals.stocks.path}")
	private String stockPath;

	@RequestMapping(value = "/getAllStocks", method = RequestMethod.GET)
	public ResponseEntity<StockModelListResponse> getAllStocks(HttpServletRequest request) {

		StockModelListResponse response = new StockModelListResponse();
		if (!CommonUtils.checkAuthentication(request)) {
			response.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<StockModelListResponse>(response, HttpStatus.OK);

		}

		String sql = "SELECT * from opticals_stocks;";
		List<StockModel> modelList = stocksDAO.findStocks(sql);
		response.setStockModelList(modelList);
		return new ResponseEntity<StockModelListResponse>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getStocksType", method = RequestMethod.GET)
	public List<String> getStocksType(HttpServletRequest request) {
		String sql = "SELECT DISTINCT(product_type) from opticals_stocks;";
		List<String> modelList = stocksDAO.query(sql);
		return modelList;
	}

	@RequestMapping(value = "/getStocksbyType/{type}", method = RequestMethod.GET)
	public ResponseEntity<StockModelListResponse> getSotcksbyType(HttpServletRequest request,
			@PathVariable("type") String productType) {
		StockModelListResponse response = new StockModelListResponse();
		if (!CommonUtils.checkAuthentication(request)) {
			response.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<StockModelListResponse>(response, HttpStatus.OK);

		}

		String sql = "SELECT * from opticals_stocks WHERE product_type LIKE " + " '" + productType + "';";

		response.setStockModelList(stocksDAO.findStocks(sql));
		return new ResponseEntity<StockModelListResponse>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/getStocksbyId/{productId}", method = RequestMethod.GET)
	public ResponseEntity<StockModel> getStocksbyId(HttpServletRequest request,
			@PathVariable("productId") Integer productId) {
		StockModel stockModel = new StockModel();
		if (!CommonUtils.checkAuthentication(request)) {
			stockModel.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<StockModel>(stockModel, HttpStatus.OK);

		}
		/*
		 * String sql = "SELECT * from opticals_stocks WHERE product_id=" + productId +
		 * ";";
		 */

		StockModel model = (StockModel) JSONFileHandler.readJsonFile(stockPath, "", productId + ".json",
				StockModel.class);

		return new ResponseEntity<StockModel>(model, HttpStatus.OK);
	}

	@RequestMapping(value = "/getStocksbyCode/{code}", method = RequestMethod.GET)
	public ResponseEntity<StockModel> getStocksbyCode(HttpServletRequest request,
			@PathVariable("code") String productCode) {
		return null;
	}

	@RequestMapping(value = "/updateStocks", method = RequestMethod.POST)
	public ResponseEntity<AddOrUpdateStockResponse> updateStocks(HttpServletRequest reques,
			@RequestBody AddOrUpdateStockRequest request) {
		AddOrUpdateStockResponse response = new AddOrUpdateStockResponse();
		if (!CommonUtils.checkAuthentication(reques)) {
			response.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<AddOrUpdateStockResponse>(response, HttpStatus.OK);

		}

		StockModel model = updateStocks(request);
		response.setModel(model);
		response.setStatus("success");
		return new ResponseEntity<AddOrUpdateStockResponse>(response, HttpStatus.OK);
	}

	protected StockModel updateStocks(AddOrUpdateStockRequest request) {
		StockModel model = (StockModel) JSONFileHandler.readJsonFile(stockPath, "", request.getProductId() + ".json",
				StockModel.class);

		String sql = "UPDATE opticals_stocks SET product_qty= ?, display_qty=? ,update_timestamp=?  WHERE product_id=?";
		stocksDAO.addOrUpdateStocks(sql, model.getQuantity() + request.getQuantityChange(), request.getDisplayQty(),
				Timestamp.valueOf(LocalDateTime.now()), request.getProductId());

		model.setQuantity(model.getQuantity() + request.getQuantityChange());
		StockLogModel log = new StockLogModel();
		log.setQuantityChange(request.getQuantityChange());
		log.setReason(request.getReason());
		log.setDisplayQty(request.getDisplayQty());
		log.setRefId(request.getRefId());
		log.setUpdateDate(String.valueOf(Timestamp.valueOf(LocalDateTime.now())));
		log.setUser(request.getUser());

		model.getStockLogsList().add(log);
		JSONFileHandler.writeJsonFile(stockPath, "", model.getJsonFileName(), model);

		LOGGER.info("Updating stocks for productId={}, quantity={} and user={} ", request.getProductId(),
				request.getQuantityChange(), request.getUser());
		return model;
	}

	@RequestMapping(value = "/addStocks", method = RequestMethod.POST)
	public ResponseEntity<AddOrUpdateStockResponse> addStocks(HttpServletRequest reques,
			@RequestBody StockModel request) {

		LOGGER.info("Adding new  stocks for productId={},productName={} ,quantity={} ", request.getProductId(),
				request.getProductName(), request.getQuantity());
		AddOrUpdateStockResponse response = new AddOrUpdateStockResponse();
		if (!CommonUtils.checkAuthentication(reques)) {
			response.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<AddOrUpdateStockResponse>(response, HttpStatus.OK);

		}

		String sql = "INSERT INTO opticals_stocks ( product_id,product_type,product_sub_type,product_name ,product_desc"
				+ ",product_code ,product_qty ,update_timestamp  ,json_file_name ) VALUES (?,?,?,?,?,?,?,?,?)";
		stocksDAO.addOrUpdateStocks(sql, request.getProductId(), request.getProductType(), request.getProductSubType(),
				request.getProductName(), request.getProductDesc(), request.getCode(), request.getQuantity(),
				Timestamp.valueOf(LocalDateTime.now()), request.getProductId() + ".json");
		request.setJsonFileName(request.getProductId() + ".json");
		StockLogModel log = new StockLogModel();
		log.setQuantityChange(request.getQuantity());
		log.setReason("New Stock Added");
		log.setRefId("start");
		log.setUpdateDate(request.getUpdateDate());
		log.setUser("user");
		request.setStockLogsList(Arrays.asList(log));
		JSONFileHandler.writeJsonFile(stockPath, "", request.getJsonFileName(), request);

		response.setModel(request);
		response.setStatus("success");

		return new ResponseEntity<AddOrUpdateStockResponse>(response, HttpStatus.OK);
	}
}
