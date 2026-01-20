package vn.edu.haui.scheduler.infrastructure.persistence;

import vn.edu.haui.scheduler.domain.constraint.RangBuocToiUu;
import vn.edu.haui.scheduler.domain.enums.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RangBuocToiUuRepository
{
	public List<RangBuocToiUu> layRangBuocTheoYeuCau(
			Connection conn,
			long yeuCauId) throws SQLException
	{
		String sql = """
				SELECT
					loai_rang_buoc,
					la_cung,
					trong_so,
					target_type,
					target_value,
					attribute,
					operator,
					value
				FROM rang_buoc_toi_uu
				WHERE yeu_cau_id = ?
				""";

		List<RangBuocToiUu> ketQua = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, yeuCauId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {

					RangBuocToiUu rb = new RangBuocToiUu(
							LoaiRangBuoc.valueOf(
									rs.getString("loai_rang_buoc")),

							rs.getBoolean("la_cung"),

							rs.getDouble("trong_so"),

							TargetType.valueOf(
									rs.getString("target_type")),

							rs.getString("target_value"),

							rs.getString("attribute"),

							ToanTuSoSanh.valueOf(
									rs.getString("operator")),

							rs.getString("value"));

					ketQua.add(rb);
				}
			}
		}
		return List.copyOf(ketQua);
	}
}
