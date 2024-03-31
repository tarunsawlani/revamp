package com.amaropticals.restcontroller;

import com.amaropticals.common.AOConstants;
import com.amaropticals.common.CommonUtils;
import com.amaropticals.common.MailUtils;
import com.amaropticals.dao.GenericDAO;
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

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/queries")
@RestController
public class QueryController {

	@Autowired
	private GenericDAO genericDAO;

	private final Logger LOGGER = LoggerFactory.getLogger(QueryController.class);

	@Value("${amaropticals.reports.email}")
	private String emailAddresses;

	@RequestMapping(value = "/createQuery", method = RequestMethod.POST)
	public ResponseEntity<QueryModel> createQuery(HttpServletRequest request,@RequestBody QueryModel model) {
		QueryModel response = new QueryModel();
		if (!CommonUtils.checkAuthentication(request)) {
			response.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<QueryModel>(response, HttpStatus.OK);

		}

		model.setQueryId("Q"+String.valueOf(CommonUtils.getNextQueryId()));
		model.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()).toString());

		LOGGER.info("Adding queryId={}", model.getQueryId());

		String sql = "INSERT INTO opticals_query (query_id, reason, name, created_by, contact, update_timestamp)"
				+ " VALUES(?,?,?,?,?,?);";

		genericDAO.addOrUpdateInvoice(sql,model.getQueryId(), model.getReason(), model.getName(),
				model.getCreatedBy(), model.getContact(), model.getUpdateTime());
		MailUtils.sendMail(emailAddresses, model.getQueryId()+"/"+model.getReason()+"/"+model.getCreatedBy(), model);
		response.setName(model.getName());
		response.setQueryId(model.getQueryId());
		response.setReason(model.getReason());
		response.setName(model.getName());
		response.setCreatedBy(model.getCreatedBy());
		response.setUpdateTime(model.getUpdateTime());
		 return new ResponseEntity<QueryModel>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/searchQuery", method = RequestMethod.POST)
	public ResponseEntity<QueryModelList> searchQuery(HttpServletRequest request, @RequestBody QueryModel model) {
		QueryModelList response = new QueryModelList();
		if (!CommonUtils.checkAuthentication(request)) {
			response.setError(new AOError(2, "Un-Authorized access.Please login again"));
			return new ResponseEntity<QueryModelList>(response, HttpStatus.OK);

		}
		List<QueryModel> queryModelList = searchQueryLogic(model);
		response.setQueryModelList(queryModelList);
		return new ResponseEntity<QueryModelList>(response, HttpStatus.OK);
	}

	private List<QueryModel> searchQueryLogic(QueryModel model) {
		LOGGER.info("Search Tasking queryId={}, name={}", model.getQueryId(), model.getName());

		String sql = "SELECT * from opticals_query;";

		if (StringUtils.isNotBlank(model.getQueryId())) {
			sql = "SELECT * from opticals_query WHERE query_id LIKE  '" + model.getQueryId() + "%';";
		} else if (StringUtils.isNotBlank(model.getName())) {
			sql = "SELECT * from opticals_query WHERE name LIKE '" + model.getName() + "';";
		} else if (StringUtils.isNotBlank(model.getName())) {
			sql = "SELECT * from opticals_query WHERE reason LIKE '" + model.getReason() + "';";
		} else if (StringUtils.isNotBlank(model.getUpdateTime())) {

			sql = "SELECT * from opticals_query WHERE update_timestamp > '" + Date.valueOf(model.getUpdateTime()) + "';";

		} else {

			sql = "SELECT * from opticals_query ;";
		}
	List<QueryModel>	queryModelList = genericDAO.findQuery(sql);
		return queryModelList;
	}

}
