package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.domain.model.GiangVien;
import java.sql.ResultSet;

public class GiangVienJdbcMapper
{
	public static GiangVien toDomain(ResultSet rs) throws Exception
	{
		return GiangVien.reconstruct(
				rs.getLong("id"),
				rs.getString("ten_giang_vien"));
	}
}