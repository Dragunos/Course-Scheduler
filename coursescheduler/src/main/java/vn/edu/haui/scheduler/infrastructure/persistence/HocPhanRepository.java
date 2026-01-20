package vn.edu.haui.scheduler.infrastructure.persistence;

import vn.edu.haui.scheduler.domain.model.HocPhan;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class HocPhanRepository
{

	public Map<Long, HocPhan> layTatCaHocPhan(Connection conn) throws SQLException
	{
		String sql = "SELECT id, ma_hoc_phan, ten_hoc_phan, so_tin_chi FROM hoc_phan";

		Map<Long, HocPhan> ketQua = new HashMap<>();

		try (PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while(rs.next()) {
				HocPhan hp = new HocPhan();
				hp.setId(rs.getLong("id"));
				hp.setMaHocPhan(rs.getString("ma_hoc_phan"));
				hp.setTenHocPhan(rs.getString("ten_hoc_phan"));
				hp.setSoTinChi(rs.getInt("so_tin_chi"));

				ketQua.put(hp.getId(), hp);
			}
		}
		return ketQua;
	}
}
