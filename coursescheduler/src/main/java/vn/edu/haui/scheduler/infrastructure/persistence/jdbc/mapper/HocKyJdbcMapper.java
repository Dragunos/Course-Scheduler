package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.domain.model.HocKy;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HocKyJdbcMapper
{
	public static HocKy toDomain(ResultSet rs)
	{
		try {
			return HocKy.reconstruct(
					rs.getLong("hk_id"),
					rs.getString("ten_hoc_ky"),
					rs.getString("nam_hoc"));
		}
		catch(SQLException e) {
			throw new DataAccessException("Error mapping HocKy", e);
		}
	}
}