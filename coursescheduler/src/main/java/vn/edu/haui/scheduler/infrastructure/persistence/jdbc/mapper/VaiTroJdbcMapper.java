package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.domain.model.VaiTro;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VaiTroJdbcMapper
{
	public static VaiTro toDomain(ResultSet rs)
	{
		try {
			return VaiTro.reconstruct(rs.getLong("id"), rs.getString("ten_vai_tro"));
		}
		catch(SQLException e) {
			throw new DataAccessException("Error mapping VaiTro", e);
		}
	}
}
