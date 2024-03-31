package com.amaropticals.daomapper;

import com.amaropticals.model.QueryModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryModelMapper implements RowMapper<QueryModel> {

	@Override
	public QueryModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		QueryModel model = new QueryModel();
		model.setQueryId(rs.getString("query_id"));
		model.setReason(rs.getString("reason"));
		model.setName(String.valueOf(rs.getDate("name")));
		model.setUpdateTime(String.valueOf(rs.getTimestamp("update_timestamp")));
		model.setCreatedBy(rs.getString("created_by"));
		return model;
	}

}
