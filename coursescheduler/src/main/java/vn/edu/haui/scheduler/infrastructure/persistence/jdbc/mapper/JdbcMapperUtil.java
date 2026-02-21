package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public final class JdbcMapperUtil
{
	private JdbcMapperUtil()
	{
	}

	public static Long getLong(ResultSet rs, String column) throws SQLException
	{
		long value = rs.getLong(column);
		return rs.wasNull() ? null : value;
	}

	public static Integer getInt(ResultSet rs, String column) throws SQLException
	{
		int value = rs.getInt(column);
		return rs.wasNull() ? null : value;
	}

	public static Double getDouble(ResultSet rs, String column) throws SQLException
	{
		double value = rs.getDouble(column);
		return rs.wasNull() ? null : value;
	}

	public static LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException
	{
		var ts = rs.getTimestamp(column);
		return ts == null ? null : ts.toLocalDateTime();
	}
}