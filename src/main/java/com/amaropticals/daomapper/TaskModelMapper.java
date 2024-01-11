package com.amaropticals.daomapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.amaropticals.model.TaskModel;

public class TaskModelMapper implements RowMapper<TaskModel> {

	@Override
	public TaskModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		TaskModel model = new TaskModel();
		model.setTaskId(rs.getString("task_id"));
		model.setTaskStatus(rs.getString("task_status"));
		model.setDeliveryDate(String.valueOf(rs.getDate("delivery_date")));
		model.setUpdateTime(String.valueOf(rs.getTimestamp("update_timestamp")));
		model.setUser(rs.getString("user"));
		model.setName(rs.getString("name"));
        model.setBalanceAmount(Double.toString(rs.getDouble("balance_amount")));
        model.setUpdatedBy(rs.getString("update_user"));
        model.setPaymentMode(rs.getString("payment_mode"));
        model.setComments(rs.getString("comments"));
		return model;
	}

}
