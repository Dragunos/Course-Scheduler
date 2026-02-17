package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.YeuCauRepositoryPort;
import vn.edu.haui.scheduler.domain.model.YeuCau;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcYeuCauRepository implements YeuCauRepositoryPort
{
	@Override
	public long save(long nguoiTaoId,
			long danhSachLopId,
			String tenYeuCau) throws Exception
	{
		String sql = """
				INSERT INTO yeu_cau
				(nguoi_tao_id, danh_sach_lop_id, ten_yeu_cau)
				VALUES (?, ?, ?)
				""";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setLong(1, nguoiTaoId);
			ps.setLong(2, danhSachLopId);
			ps.setString(3, tenYeuCau);

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next())
					return rs.getLong(1);
			}
		}

		throw new RuntimeException("Cannot create yeu_cau");
	}

	@Override
	public Optional<YeuCau> findById(long id) throws Exception
	{
		String sql = """
				SELECT id, nguoi_tao_id, danh_sach_lop_id,
				       ten_yeu_cau, ngay_tao
				FROM yeu_cau
				WHERE id = ?
				""";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {

					YeuCau yc = new YeuCau();

					yc.setId(rs.getLong("id"));
					yc.setNguoiTaoId(rs.getLong("nguoi_tao_id"));
					yc.setDanhSachLopId(rs.getLong("danh_sach_lop_id"));
					yc.setTenYeuCau(rs.getString("ten_yeu_cau"));

					Timestamp ts = rs.getTimestamp("ngay_tao");
					if(ts != null)
						yc.setNgayTao(ts.toLocalDateTime());

					return Optional.of(yc);
				}
			}
		}

		return Optional.empty();
	}

	@Override
	public List<YeuCau> findByNguoiTaoId(long nguoiTaoId) throws Exception
	{
		String sql = """
				SELECT id, nguoi_tao_id, danh_sach_lop_id,
				       ten_yeu_cau, ngay_tao
				FROM yeu_cau
				WHERE nguoi_tao_id = ?
				ORDER BY ngay_tao DESC
				""";

		List<YeuCau> list = new ArrayList<>();

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, nguoiTaoId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {

					YeuCau yc = new YeuCau();

					yc.setId(rs.getLong("id"));
					yc.setNguoiTaoId(rs.getLong("nguoi_tao_id"));
					yc.setDanhSachLopId(rs.getLong("danh_sach_lop_id"));
					yc.setTenYeuCau(rs.getString("ten_yeu_cau"));

					Timestamp ts = rs.getTimestamp("ngay_tao");
					if(ts != null)
						yc.setNgayTao(ts.toLocalDateTime());

					list.add(yc);
				}
			}
		}

		return list;
	}

	@Override
	public void deleteById(long id) throws Exception
	{
		String sql = "DELETE FROM yeu_cau WHERE id = ?";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);
			ps.executeUpdate();
		}
	}

	@Override
	public long createFromThoiKhoaBieu(long thoiKhoaBieuId) throws Exception
	{
		Connection conn = null;

		try {
			conn = DataSourceProvider.getDataSource().getConnection();
			conn.setAutoCommit(false);

			String selectTkbSql = """
					SELECT nguoi_dung_id, danh_sach_lop_id, ten_phuong_an
					FROM thoi_khoa_bieu
					WHERE id = ?
					""";

			long nguoiDungId;
			long danhSachLopId;
			String tenPhuongAn;

			try (PreparedStatement ps = conn.prepareStatement(selectTkbSql)) {

				ps.setLong(1, thoiKhoaBieuId);

				try (ResultSet rs = ps.executeQuery()) {

					if(!rs.next())
						throw new RuntimeException("Không tìm thấy thời khóa biểu");

					nguoiDungId = rs.getLong("nguoi_dung_id");
					danhSachLopId = rs.getLong("danh_sach_lop_id");
					tenPhuongAn = rs.getString("ten_phuong_an");
				}
			}

			String insertYeuCauSql = """
					INSERT INTO yeu_cau
					(nguoi_tao_id, danh_sach_lop_id, ten_yeu_cau)
					VALUES (?, ?, ?)
					""";

			long newYeuCauId;

			try (PreparedStatement ps = conn.prepareStatement(
					insertYeuCauSql,
					Statement.RETURN_GENERATED_KEYS)) {

				ps.setLong(1, nguoiDungId);
				ps.setLong(2, danhSachLopId);
				ps.setString(3, "Tối ưu lại từ: " + tenPhuongAn);

				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if(rs.next())
						newYeuCauId = rs.getLong(1);
					else
						throw new RuntimeException("Không tạo được yêu cầu mới");
				}
			}

			String selectChiTietSql = """
					SELECT lop_hoc_phan_id
					FROM thoi_khoa_bieu_chi_tiet
					WHERE thoi_khoa_bieu_id = ?
					""";

			List<Long> lopIds = new ArrayList<>();

			try (PreparedStatement ps = conn.prepareStatement(selectChiTietSql)) {

				ps.setLong(1, thoiKhoaBieuId);

				try (ResultSet rs = ps.executeQuery()) {
					while(rs.next()) {
						lopIds.add(rs.getLong("lop_hoc_phan_id"));
					}
				}
			}

			String insertChiTietSql = """
					INSERT INTO yeu_cau_chi_tiet
					(yeu_cau_id, lop_hoc_phan_id, bat_buoc, loai_chi_dinh)
					VALUES (?, ?, 1, 'REQUIRE')
					""";

			try (PreparedStatement ps = conn.prepareStatement(insertChiTietSql)) {

				for(Long lopId : lopIds) {

					ps.setLong(1, newYeuCauId);
					ps.setLong(2, lopId);
					ps.addBatch();
				}

				ps.executeBatch();
			}

			conn.commit();
			return newYeuCauId;

		}
		catch(Exception ex) {

			if(conn != null)
				conn.rollback();

			throw ex;

		}
		finally {

			if(conn != null)
				conn.close();
		}
	}
}
