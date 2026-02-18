package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.LichHocRepository;
import vn.edu.haui.scheduler.domain.enums.ThuTrongTuan;
import vn.edu.haui.scheduler.domain.model.LichHoc;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcLichHocRepository implements LichHocRepository
{
	@Override
	public void saveAll(Long lopHocPhanId, List<LichHoc> lichHocs) throws SQLException
	{
		if(lichHocs == null || lichHocs.isEmpty()) return;

		String insert = "INSERT INTO lich_hoc (lop_hoc_phan_id, thu, tiet_bat_dau, tiet_ket_thuc) VALUES (?,?,?,?)";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(insert)) {

			for(LichHoc lh : lichHocs) {
				ps.setLong(1, lopHocPhanId);
				ps.setInt(2, lh.getThu().getGiaTri());
				ps.setInt(3, lh.getTietBatDau());
				ps.setInt(4, lh.getTietKetThuc());
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	@Override
	public List<LichHoc> findByLopHocPhanId(Long lopHocPhanId) throws Exception
	{
		String sql = "SELECT thu, tiet_bat_dau, tiet_ket_thuc FROM lich_hoc WHERE lop_hoc_phan_id = ?";

		List<LichHoc> result = new ArrayList<>();

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, lopHocPhanId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					LichHoc lh = new LichHoc();
					lh.setThu(ThuTrongTuan.fromGiaTri(rs.getInt("thu")));
					lh.setTietBatDau(rs.getInt("tiet_bat_dau"));
					lh.setTietKetThuc(rs.getInt("tiet_ket_thuc"));
					result.add(lh);
				}
			}
		}
		return result;
	}

	@Override
	public Map<Long, List<LichHoc>> findByLopHocPhanIds(List<Long> lopHocPhanIds) throws Exception
	{
		if(lopHocPhanIds == null || lopHocPhanIds.isEmpty())
			return Map.of();

		String placeholders = lopHocPhanIds.stream()
				.map(id -> "?")
				.reduce((a, b) -> a + "," + b)
				.get();

		String sql = "SELECT lop_hoc_phan_id, thu, tiet_bat_dau, tiet_ket_thuc " +
				"FROM lich_hoc WHERE lop_hoc_phan_id IN (" + placeholders + ")";

		Map<Long, List<LichHoc>> result = new HashMap<>();

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			for(int i = 0; i < lopHocPhanIds.size(); i++)
				ps.setLong(i + 1, lopHocPhanIds.get(i));

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {

					Long lopId = rs.getLong("lop_hoc_phan_id");

					LichHoc lh = new LichHoc();
					lh.setLopHocPhanId(lopId);
					lh.setThu(ThuTrongTuan.fromGiaTri(rs.getInt("thu")));
					lh.setTietBatDau(rs.getInt("tiet_bat_dau"));
					lh.setTietKetThuc(rs.getInt("tiet_ket_thuc"));

					result
							.computeIfAbsent(lopId, k -> new ArrayList<>())
							.add(lh);
				}
			}
		}

		return result;
	}
}
