package com.amaropticals.model;

import java.util.List;

public class QueryModelList extends CommonResponseModel {

	List<QueryModel> queryModelList;

	public List<QueryModel> getQueryModelList() {
		return queryModelList;
	}

	public void setQueryModelList(List<QueryModel> queryModelList) {
		this.queryModelList = queryModelList;
	}

}
