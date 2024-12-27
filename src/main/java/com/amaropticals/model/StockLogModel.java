package com.amaropticals.model;

public class StockLogModel {
	private int quantityChange;

	private int displayQty;
	private String reason;
	private String refId;
	private String updateDate;
	private String user;
	public int getQuantityChange() {
		return quantityChange;
	}
	public void setQuantityChange(int quantityChange) {
		this.quantityChange = quantityChange;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getUser() {
		return user;
	}

	public int getDisplayQty() {
		return displayQty;
	}

	public void setDisplayQty(int displayQty) {
		this.displayQty = displayQty;
	}

	public void setUser(String user) {
		this.user = user;
	}


}

