package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import java.sql.ResultSet;

import vn.edu.haui.scheduler.domain.model.DanhSachLopChiTiet;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;

public class DanhSachLopChiTietJdbcMapper
{
	public static DanhSachLopChiTiet toDomain(
			ResultSet rs,
			LopHocPhan lopHocPhan) throws Exception
	{
		return DanhSachLopChiTiet.create(
				lopHocPhan,
				rs.getInt("bat_buoc") == 1);
	}
}