package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import java.sql.ResultSet;
import java.util.Collections;

public class LopHocPhanJdbcMapper
{
	public static LopHocPhan toDomain(ResultSet rs) throws Exception
	{
		return LopHocPhan.reconstruct(
				rs.getLong("id"),
				rs.getString("ma_lop"),
				null,
				null,
				rs.getString("hinh_thuc_day"),
				rs.getString("dia_diem"),
				Collections.emptyList());
	}
}