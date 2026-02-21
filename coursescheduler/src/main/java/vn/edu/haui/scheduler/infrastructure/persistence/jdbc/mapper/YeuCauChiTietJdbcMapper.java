package vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper;

import vn.edu.haui.scheduler.domain.model.*;

import java.sql.ResultSet;

public final class YeuCauChiTietJdbcMapper
{
	private YeuCauChiTietJdbcMapper()
	{
	}

	public static YeuCauChiTiet toDomain(ResultSet rs) throws Exception
	{
		if(rs == null) return null;

		YeuCau yeuCau = YeuCauJdbcMapper.toDomain(rs);

		LopHocPhan lopHocPhan = LopHocPhanJdbcMapper.toDomain(rs);

		return YeuCauChiTiet.reconstruct(
				yeuCau,
				lopHocPhan,
				rs.getInt("bat_buoc") == 1,
				rs.getString("loai_chi_dinh"),
				JdbcMapperUtil.getDouble(rs, "trong_so"));
	}
}