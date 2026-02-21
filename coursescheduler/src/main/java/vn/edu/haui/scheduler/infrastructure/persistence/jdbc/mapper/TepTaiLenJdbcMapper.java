package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.domain.model.TepTaiLen;
import vn.edu.haui.scheduler.domain.model.NguoiDung;

import java.sql.ResultSet;

public class TepTaiLenJdbcMapper
{
	public static TepTaiLen toDomain(ResultSet rs) throws Exception
	{
		if(rs == null)
			return null;

		NguoiDung nguoiDung = null;

		Long nguoiDungId = JdbcMapperUtil.getLong(rs, "nguoi_dung_id");

		if(nguoiDungId != null) {
			nguoiDung = NguoiDungJdbcMapper.toDomain(rs);
		}

		return TepTaiLen.reconstruct(
				rs.getLong("id"),
				nguoiDung,
				rs.getString("ten_tep_goc"),
				rs.getString("loai_tep"),
				rs.getString("duong_dan"),
				rs.getString("storage_type"),
				rs.getBytes("file_blob"),
				rs.getString("checksum"),
				JdbcMapperUtil.getLong(rs, "kich_thuoc"),
				JdbcMapperUtil.getLocalDateTime(rs, "ngay_tao"));
	}
}