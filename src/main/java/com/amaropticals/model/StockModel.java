package com.amaropticals.model;

import java.util.List;

public class StockModel extends CommonResponseModel {
	
	private int productId;
	private String productType;
	private String productSubType;
	private String productName;

	public int getDisplayQty() {
		return displayQty;
	}

	public void setDisplayQty(int displayQty) {
		this.displayQty = displayQty;
	}

	private String productDesc;
	private int quantity;

	private int displayQty;
	private String updateDate;
	private String code;
	private String jsonFileName;
	private List <StockLogModel> stockLogsList;
	
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getJsonFileName() {
		return jsonFileName;
	}
	public void setJsonFileName(String jsonFileName) {
		this.jsonFileName = jsonFileName;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getProductSubType() {
		return productSubType;
	}
	public void setProductSubType(String productSubType) {
		this.productSubType = productSubType;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public List<StockLogModel> getStockLogsList() {
		return stockLogsList;
	}
	public void setStockLogsList(List<StockLogModel> stockLogsList) {
		this.stockLogsList = stockLogsList;
	}
	

}
