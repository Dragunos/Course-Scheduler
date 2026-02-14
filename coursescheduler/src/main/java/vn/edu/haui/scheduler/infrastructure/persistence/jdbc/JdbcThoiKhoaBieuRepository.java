package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.ThoiKhoaBieuRepositoryPort;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcThoiKhoaBieuRepository implements ThoiKhoaBieuRepositoryPort
{
	@Override
	public long save(long nguoiDungId,
			long danhSachLopId,
			String tenPhuongAn,
			double diem) throws Exception
	{
		String sql = """
				    INSERT INTO thoi_khoa_bieu
				    (nguoi_dung_id, danh_sach_lop_id, ten_phuong_an, diem_danh_gia)
				    VALUES (?, ?, ?, ?)
				""";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, nguoiDungId);
			ps.setLong(2, danhSachLopId);
			ps.setString(3, tenPhuongAn);
			ps.setDouble(4, diem);

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) return rs.getLong(1);
			}
		}

		throw new RuntimeException("Cannot create thoi_khoa_bieu");
	}

	@Override
	public void saveChiTiet(long id,
			List<Long> lopIds) throws Exception
	{
		String sql = """
				    INSERT INTO thoi_khoa_bieu_chi_tiet
				    (thoi_khoa_bieu_id, lop_hoc_phan_id)
				    VALUES (?,?)
				""";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for(Long lopId : lopIds) {
				ps.setLong(1, id);
				ps.setLong(2, lopId);
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	@Override
	public List<Long> findChiTietByThoiKhoaBieuId(long id) throws Exception
	{
		String sql = """
				    SELECT lop_hoc_phan_id
				    FROM thoi_khoa_bieu_chi_tiet
				    WHERE thoi_khoa_bieu_id = ?
				""";

		List<Long> result = new ArrayList<>();

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next())
					result.add(rs.getLong(1));
			}
		}

		return result;
	}

	@Override
	public long findDanhSachLopId(long id) throws Exception
	{
		String sql = "SELECT danh_sach_lop_id FROM thoi_khoa_bieu WHERE id = ?";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next())
					return rs.getLong(1);
			}
		}

		throw new RuntimeException("Not found");
	}
}
