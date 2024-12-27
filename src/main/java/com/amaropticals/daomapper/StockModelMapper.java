package com.amaropticals.daomapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.amaropticals.model.StockModel;

public class StockModelMapper implements RowMapper<StockModel> {

	@Override
	public StockModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		StockModel model = new StockModel();
		model.setProductId(rs.getInt("product_id"));
		model.setProductType(rs.getString("product_type"));
		model.setProductSubType(rs.getString("product_sub_type"));
		model.setProductName(rs.getString("product_name"));
		model.setProductDesc(rs.getString("product_desc"));
		model.setQuantity(rs.getInt("product_qty"));
		model.setUpdateDate(String.valueOf(rs.getTimestamp("update_timestamp")));
		model.setCode(rs.getString("product_code"));
		model.setJsonFileName(rs.getString("json_file_name"));
		model.setDisplayQty(rs.getInt("display_qty"));
		return model;
	}

}
