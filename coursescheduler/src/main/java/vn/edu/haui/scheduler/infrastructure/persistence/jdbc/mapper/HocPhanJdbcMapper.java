package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.domain.model.HocPhan;
import java.sql.ResultSet;

public class HocPhanJdbcMapper
{
	public static HocPhan toDomain(ResultSet rs) throws Exception
	{
		return HocPhan.reconstruct(
				rs.getLong("id"),
				rs.getString("ma_hoc_phan"),
				rs.getString("ten_hoc_phan"),
				JdbcMapperUtil.getInt(rs, "so_tin_chi"));
	}
}