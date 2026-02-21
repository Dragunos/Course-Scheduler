package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.domain.model.HocKy;
import java.sql.ResultSet;

public class HocKyJdbcMapper
{
	public static HocKy toDomain(ResultSet rs) throws Exception
	{
		return HocKy.reconstruct(
				rs.getLong("id"),
				rs.getString("ten_hoc_ky"),
				rs.getString("nam_hoc"));
	}
}