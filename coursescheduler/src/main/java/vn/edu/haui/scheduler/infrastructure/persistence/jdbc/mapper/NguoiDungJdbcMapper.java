package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.VaiTro;

import java.sql.ResultSet;

public class NguoiDungJdbcMapper
{
	public static NguoiDung toDomain(ResultSet rs) throws Exception
	{
		if(rs == null)
			return null;

		VaiTro vaiTro = null;

		Long vaiTroId = JdbcMapperUtil.getLong(rs, "vt_id");

		if(vaiTroId != null) {
			vaiTro = VaiTroJdbcMapper.toDomain(rs);
		}

		return NguoiDung.reconstruct(
				rs.getLong("nd_id"),
				rs.getString("ten_dang_nhap"),
				rs.getString("mat_khau_hash"),
				vaiTro,
				JdbcMapperUtil.getLocalDateTime(rs, "nd_ngay_tao"));
	}
}