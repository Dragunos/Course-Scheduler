package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.infrastructure.persistence.NguoiDungRepository;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class JdbcNguoiDungRepository implements NguoiDungRepository
{

	@Override
	public Optional<NguoiDung> timTheoTenDangNhap(String tenDangNhap)
	{

		String sql = """
				SELECT id, ten_dang_nhap, mat_khau_hash, role_id, ngay_tao
				FROM nguoi_dung
				WHERE ten_dang_nhap = ?
				""";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, tenDangNhap);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					NguoiDung nd = new NguoiDung(
							rs.getInt("id"),
							rs.getString("ten_dang_nhap"),
							rs.getString("mat_khau_hash"),
							rs.getInt("role_id"),
							rs.getTimestamp("ngay_tao").toLocalDateTime());
					return Optional.of(nd);
				}
			}

			return Optional.empty();
		}
		catch(Exception e) {
			throw new RuntimeException("Lỗi khi tìm người dùng theo tên đăng nhập", e);
		}
	}

	@Override
	public long luu(NguoiDung nguoiDung) throws Exception
	{

		String sql = """
				INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau_hash, role_id)
				VALUES (?, ?, ?)
				""";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, nguoiDung.getTenDangNhap());
			ps.setString(2, nguoiDung.getMatKhauHash());
			ps.setInt(3, nguoiDung.getVaiTroId());

			int affected = ps.executeUpdate();
			if(affected == 0) {
				throw new RuntimeException("Insert user failed, no rows affected");
			}

			try (ResultSet keys = ps.getGeneratedKeys()) {
				if(keys.next()) {
					return keys.getLong(1);
				}
				else {
					throw new RuntimeException("Insert user failed, no ID obtained");
				}
			}
		}
	}
}
