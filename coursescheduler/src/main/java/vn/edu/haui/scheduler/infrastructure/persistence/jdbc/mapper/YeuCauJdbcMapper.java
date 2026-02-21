package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.domain.model.*;

import java.sql.ResultSet;

public class YeuCauJdbcMapper
{
	public static YeuCau toDomain(ResultSet rs) throws Exception
	{
		if(rs == null) return null;

		NguoiDung nguoiTao = null;

		Long nguoiTaoId = JdbcMapperUtil.getLong(rs, "nguoi_tao_id");

		if(nguoiTaoId != null) {
			nguoiTao = NguoiDungJdbcMapper.toDomain(rs);
		}

		HocKy hocKy = null;

		Long hocKyId = JdbcMapperUtil.getLong(rs, "hoc_ky_id");

		if(hocKyId != null) {
			hocKy = HocKyJdbcMapper.toDomain(rs);
		}

		DanhSachLop danhSachLop = null;

		Long dsId = JdbcMapperUtil.getLong(rs, "danh_sach_lop_id");

		if(dsId != null) {
			danhSachLop = DanhSachLopJdbcMapper.toDomain(
					rs,
					nguoiTao,
					hocKy,
					null);
		}

		return YeuCau.reconstruct(
				rs.getLong("id"),
				nguoiTao,
				danhSachLop,
				rs.getString("ten_yeu_cau"),
				JdbcMapperUtil.getLocalDateTime(rs, "ngay_tao"),
				null,
				null);
	}
}