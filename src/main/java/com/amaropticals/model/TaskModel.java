package com.amaropticals.model;

public class TaskModel {
	
private String taskId;
private String taskStatus;
private String name;
private String deliveryDate;
private String updateTime;

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	private String user;

private String updatedBy;

private String paymentMode;

private String balanceAmount;

private String comments;

public String getTaskId() {
	return taskId;
}
public void setTaskId(String taskId) {
	this.taskId = taskId;
}
public String getTaskStatus() {
	return taskStatus;
}
public void setTaskStatus(String taskStatus) {
	this.taskStatus = taskStatus;
}
public String getDeliveryDate() {
	return deliveryDate;
}
public void setDeliveryDate(String deliveryDate) {
	this.deliveryDate = deliveryDate;
}
public String getUpdateTime() {
	return updateTime;
}
public void setUpdateTime(String updateTime) {
	this.updateTime = updateTime;
}
public String getUser() {
	return user;
}
public void setUser(String user) {
	this.user = user;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(String balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
}
