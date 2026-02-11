package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.GiangVienRepositoryPort;

import java.sql.*;
import java.util.Optional;

public class JdbcGiangVienRepository implements GiangVienRepositoryPort
{
	@Override
	public Optional<Integer> findIdByTen(Connection conn, String tenGiangVien) throws SQLException
	{
		if(tenGiangVien == null || tenGiangVien.isEmpty()) return Optional.empty();
		String sql = "SELECT id FROM giang_vien WHERE ten_giang_vien = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, tenGiangVien);
			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) return Optional.of(rs.getInt(1));
			}
		}
		return Optional.empty();
	}

	@Override
	public int save(Connection conn, String tenGiangVien) throws SQLException
	{
		String insert = "INSERT INTO giang_vien (ten_giang_vien) VALUES (?)";
		try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, tenGiangVien);
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) return rs.getInt(1);
			}
		}
		throw new SQLException("Cannot create giang_vien");
	}
}