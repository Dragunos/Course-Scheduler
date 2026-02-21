package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import java.sql.ResultSet;
import java.util.List;

import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.DanhSachLopChiTiet;
import vn.edu.haui.scheduler.domain.model.HocKy;
import vn.edu.haui.scheduler.domain.model.NguoiDung;

public class DanhSachLopJdbcMapper
{
	public static DanhSachLop toDomain(
			ResultSet rs,
			NguoiDung nguoiTao,
			HocKy hocKy,
			List<DanhSachLopChiTiet> chiTiet) throws Exception
	{
		return DanhSachLop.reconstruct(
				rs.getLong("id"),
				rs.getString("ten_danh_sach"),
				nguoiTao,
				rs.getInt("la_cong_khai") == 1,
				hocKy,
				JdbcMapperUtil.getLocalDateTime(rs, "ngay_tao"),
				chiTiet);
	}
}