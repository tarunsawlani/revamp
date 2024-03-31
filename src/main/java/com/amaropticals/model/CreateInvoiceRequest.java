package com.amaropticals.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="invoice")
public class CreateInvoiceRequest extends CommonResponseModel {

	
	private long invoiceId;
	
	private String name;
	
	private String email;
	private String contact;
	private String refDr;
	private String updateDate;
	private String deliveryDate;
	private String totalAmount;
	private String initialAmount;
	private String pendingAmount;
	private String jsonFileName;
	private String comments;
	private boolean withoutDetailsInvoice;
	public String getJsonFileName() {
		return jsonFileName;
	}
	public void setJsonFileName(String jsonFileName) {
		this.jsonFileName = jsonFileName;
	}
	private List<ItemModel> purchaseItems;
	private String createdBy;
	private String paymentMode;

	private String fileName;
	private String fileType;
	private String imgBase64String;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getImgBase64String() {
		return imgBase64String;
	}

	public void setImgBase64String(String imgBase64String) {
		this.imgBase64String = imgBase64String;
	}

	@XmlElement(name="invoiceid")
	public long getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}
	@XmlElement(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name="email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@XmlElement(name="contact")
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	
	@XmlElement(name="referredby")
	public String getRefDr() {
		return refDr;
	}
	public void setRefDr(String refDr) {
		this.refDr = refDr;
	}
	
	@XmlElement(name="updatedate")
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	
	@XmlElement(name="deliverydate")
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	
	@XmlElement(name="totalamount")
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	@XmlElement(name="initialamount")
	public String getInitialAmount() {
		return initialAmount;
	}
	public void setInitialAmount(String initalAmount) {
		this.initialAmount = initalAmount;
	}
	
	@XmlElement(name="pendingamount")
	public String getPendingAmount() {
		return pendingAmount;
	}
	public void setPendingAmount(String pendingAmount) {
		this.pendingAmount = pendingAmount;
	}
	
	//@XmlElement(name="purchaseitems")
	@XmlElementWrapper(name="purchaseitems")
	@XmlElement(name="purchaseitem")
	public List<ItemModel> getPurchaseItems() {
		return purchaseItems;
	}
	public void setPurchaseItems(List<ItemModel> itemsList) {
		this.purchaseItems = itemsList;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public boolean isWithoutDetailsInvoice() {
		return withoutDetailsInvoice;
	}
	public void setWithoutDetailsInvoice(boolean withoutDetailsInvoice) {
		this.withoutDetailsInvoice = withoutDetailsInvoice;
	}

	@XmlElement(name="createdby")
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	@XmlElement(name="paymentmode")
	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	
	
}
