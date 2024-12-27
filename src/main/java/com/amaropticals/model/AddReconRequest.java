package com.amaropticals.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

public class AddReconRequest extends CommonResponseModel {


	private List<ReconModel> reconModelList;

	public List<ReconModel> getReconModelList() {
		return reconModelList;
	}

	public void setReconModelList(List<ReconModel> reconModelList) {
		this.reconModelList = reconModelList;
	}

	private String user;


	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getReconTimeStamp() {
		return reconTimeStamp;
	}

	public void setReconTimeStamp(String reconTimeStamp) {
		this.reconTimeStamp = reconTimeStamp;
	}

	private String reconTimeStamp;


	
	
}
