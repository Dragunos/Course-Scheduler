package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.ThoiKhoaBieuRepositoryPort;
import vn.edu.haui.scheduler.domain.model.PhuongAnThoiKhoaBieu;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	@Override
	public List<PhuongAnThoiKhoaBieu> findByNguoiDungId(long nguoiDungId) throws Exception
	{
		String sql = """
					SELECT id, nguoi_dung_id, danh_sach_lop_id,
					       ten_phuong_an, diem_danh_gia, ngay_tao
					FROM thoi_khoa_bieu
					WHERE nguoi_dung_id = ?
					ORDER BY ngay_tao DESC
				""";

		List<PhuongAnThoiKhoaBieu> result = new ArrayList<>();

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, nguoiDungId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					PhuongAnThoiKhoaBieu pa = new PhuongAnThoiKhoaBieu();
					pa.setId(rs.getLong("id"));
					pa.setNguoiDungId(rs.getLong("nguoi_dung_id"));
					pa.setDanhSachLopId(rs.getLong("danh_sach_lop_id"));
					pa.setTenPhuongAn(rs.getString("ten_phuong_an"));
					pa.setDiemDanhGia(rs.getDouble("diem_danh_gia"));

					Timestamp ts = rs.getTimestamp("ngay_tao");
					if(ts != null) {
						pa.setNgayTao(ts.toLocalDateTime());
					}

					result.add(pa);
				}
			}
		}
		return result;
	}

	@Override
	public Optional<PhuongAnThoiKhoaBieu> findById(long id) throws Exception
	{
		String sql = """
					SELECT id, nguoi_dung_id, danh_sach_lop_id,
					       ten_phuong_an, diem_danh_gia, ngay_tao
					FROM thoi_khoa_bieu
					WHERE id = ?
				""";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					PhuongAnThoiKhoaBieu pa = new PhuongAnThoiKhoaBieu();
					pa.setId(rs.getLong("id"));
					pa.setNguoiDungId(rs.getLong("nguoi_dung_id"));
					pa.setDanhSachLopId(rs.getLong("danh_sach_lop_id"));
					pa.setTenPhuongAn(rs.getString("ten_phuong_an"));
					pa.setDiemDanhGia(rs.getDouble("diem_danh_gia"));

					Timestamp ts = rs.getTimestamp("ngay_tao");
					if(ts != null) {
						pa.setNgayTao(ts.toLocalDateTime());
					}

					return Optional.of(pa);
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public void updateTenPhuongAn(long id, String tenMoi) throws Exception
	{
		String sql = "UPDATE thoi_khoa_bieu SET ten_phuong_an = ? WHERE id = ?";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, tenMoi);
			ps.setLong(2, id);
			ps.executeUpdate();
		}
	}

	@Override
	public void deleteById(long id) throws Exception
	{
		String deleteChiTiet = """
				DELETE FROM thoi_khoa_bieu_chi_tiet
				WHERE thoi_khoa_bieu_id = ?
				""";

		String deleteMain = """
				DELETE FROM thoi_khoa_bieu
				WHERE id = ?
				""";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection()) {

			conn.setAutoCommit(false);

			try (PreparedStatement ps1 = conn.prepareStatement(deleteChiTiet);
					PreparedStatement ps2 = conn.prepareStatement(deleteMain)) {

				ps1.setLong(1, id);
				ps1.executeUpdate();

				ps2.setLong(1, id);
				ps2.executeUpdate();

				conn.commit();
			}
			catch(Exception e) {
				conn.rollback();
				throw e;
			}
		}
	}
}
