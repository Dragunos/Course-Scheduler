package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.domain.model.*;

import java.sql.ResultSet;
import java.util.Set;

public class ThoiKhoaBieuJdbcMapper
{
	public static ThoiKhoaBieu toDomain(
			ResultSet rs,
			NguoiDung nguoiDung,
			DanhSachLop danhSachLop,
			Set<LopHocPhan> cacLop) throws Exception
	{
		return ThoiKhoaBieu.reconstruct(
				rs.getLong("id"),
				nguoiDung,
				danhSachLop,
				rs.getString("ten_phuong_an"),
				JdbcMapperUtil.getDouble(rs, "diem_danh_gia"),
				JdbcMapperUtil.getLocalDateTime(rs, "ngay_tao"),
				cacLop);
	}
}