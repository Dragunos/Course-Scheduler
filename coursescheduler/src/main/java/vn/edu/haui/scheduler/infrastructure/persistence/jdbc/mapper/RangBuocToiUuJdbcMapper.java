package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import java.sql.ResultSet;

import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.RangBuocToiUu;
import vn.edu.haui.scheduler.domain.model.ThoiKhoaBieu;
import vn.edu.haui.scheduler.domain.model.YeuCau;

public class RangBuocToiUuJdbcMapper
{
	public static RangBuocToiUu toDomain(
			ResultSet rs,
			YeuCau yeuCau,
			ThoiKhoaBieu thoiKhoaBieu,
			NguoiDung nguoiTao) throws Exception
	{
		return RangBuocToiUu.reconstruct(
				JdbcMapperUtil.getLong(rs, "id"),
				yeuCau,
				thoiKhoaBieu,
				rs.getString("loai_rang_buoc"),
				rs.getString("target_type"),
				rs.getString("target_value"),
				rs.getString("attribute"),
				rs.getString("operator"),
				rs.getString("value"),
				rs.getInt("la_cung") == 1,
				JdbcMapperUtil.getDouble(rs, "trong_so"),
				rs.getString("ghi_chu"),
				nguoiTao,
				JdbcMapperUtil.getLocalDateTime(rs, "ngay_tao"));
	}
}