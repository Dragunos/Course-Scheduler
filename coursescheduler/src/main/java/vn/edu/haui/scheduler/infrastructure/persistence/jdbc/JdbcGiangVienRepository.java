package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.GiangVienRepositoryPort;
import vn.edu.haui.scheduler.domain.model.GiangVien;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JdbcGiangVienRepository implements GiangVienRepositoryPort
{
	@Override
	public Optional<Long> findIdByTen(String tenGiangVien)
	{
		if(tenGiangVien == null || tenGiangVien.isBlank())
			return Optional.empty();

		String sql = "SELECT id FROM giang_vien WHERE ten_giang_vien = ?";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, tenGiangVien);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next())
					return Optional.of(rs.getLong("id"));
			}

		}
		catch(SQLException e) {
			throw new RuntimeException(e);
		}

		return Optional.empty();
	}

	@Override
	public Long save(GiangVien giangVien)
	{
		String sql = "INSERT INTO giang_vien (ten_giang_vien) VALUES (?)";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, giangVien.getTenGiangVien());

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next())
					return rs.getLong(1);
			}

		}
		catch(SQLException e) {
			throw new RuntimeException(e);
		}

		throw new RuntimeException("Không thể tạo giảng viên");
	}

	@Override
	public Optional<GiangVien> findById(Long id)
	{
		String sql = "SELECT id, ten_giang_vien FROM giang_vien WHERE id = ?";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					GiangVien gv = mapRow(rs);
					return Optional.of(gv);
				}
			}

		}
		catch(SQLException e) {
			throw new RuntimeException(e);
		}

		return Optional.empty();
	}

	@Override
	public List<GiangVien> findByIds(List<Long> ids)
	{
		if(ids == null || ids.isEmpty())
			return List.of();

		String placeholders = ids.stream()
				.map(i -> "?")
				.collect(Collectors.joining(","));

		String sql = "SELECT id, ten_giang_vien FROM giang_vien WHERE id IN (" + placeholders + ")";

		List<GiangVien> result = new ArrayList<>();

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			for(int i = 0; i < ids.size(); i++)
				ps.setLong(i + 1, ids.get(i));

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					result.add(mapRow(rs));
				}
			}

		}
		catch(SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	private GiangVien mapRow(ResultSet rs) throws SQLException
	{
		GiangVien gv = new GiangVien();
		gv.setId(rs.getLong("id"));
		gv.setTenGiangVien(rs.getString("ten_giang_vien"));
		return gv;
	}
}
