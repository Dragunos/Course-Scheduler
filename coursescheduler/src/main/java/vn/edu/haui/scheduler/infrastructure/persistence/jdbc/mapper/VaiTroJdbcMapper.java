package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.domain.model.VaiTro;
import java.sql.ResultSet;

public class VaiTroJdbcMapper
{
	public static VaiTro toDomain(ResultSet rs) throws Exception
	{
		return VaiTro.reconstruct(
				rs.getLong("id"),
				rs.getString("ten_vai_tro"));
	}
}