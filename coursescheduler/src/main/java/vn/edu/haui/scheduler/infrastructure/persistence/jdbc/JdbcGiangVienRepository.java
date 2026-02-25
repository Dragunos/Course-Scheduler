package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.out.GiangVienRepository;
import vn.edu.haui.scheduler.domain.model.GiangVien;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.GiangVienJdbcMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcGiangVienRepository implements GiangVienRepository
{
	public JdbcGiangVienRepository()
	{
	}

	@Override
	public GiangVien save(GiangVien giangVien)
	{
		if(giangVien == null)
			throw new ValidationException("GiangVien must not be null");

		if(giangVien.isPersisted())
			return update(giangVien);

		return insert(giangVien);
	}

	private GiangVien insert(GiangVien giangVien)
	{
		String sql = """
				INSERT INTO giang_vien (ten_giang_vien)
				VALUES (?)
				""";

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, giangVien.getTenGiangVien());

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) {
					Long id = rs.getLong(1);
					return GiangVien.reconstruct(
							id,
							giangVien.getTenGiangVien());
				}
			}

			throw new DataAccessException("Failed to retrieve generated id for GiangVien", null);
		}
		catch(SQLException e) {
			throw new DataAccessException("Error inserting GiangVien", e);
		}
	}

	private GiangVien update(GiangVien giangVien)
	{
		String sql = """
				UPDATE giang_vien
				SET ten_giang_vien = ?
				WHERE id = ?
				""";

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, giangVien.getTenGiangVien());
			ps.setLong(2, giangVien.getId());

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("GiangVien", giangVien.getId());

			return giangVien;
		}
		catch(SQLException e) {
			throw new DataAccessException("Error updating GiangVien", e);
		}
	}

	@Override
	public Optional<GiangVien> findById(Long id)
	{
		String sql = """
				SELECT
				    id AS giang_vien_id,
				    ten_giang_vien
				FROM giang_vien
				WHERE id = ?
				""";

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next())
					return Optional.of(GiangVienJdbcMapper.toDomain(rs));

				return Optional.empty();
			}
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding GiangVien by id", e);
		}
	}

	@Override
	public Optional<GiangVien> findByTen(String tenGiangVien)
	{
		String sql = """
				SELECT
				    id AS giang_vien_id,
				    ten_giang_vien
				FROM giang_vien
				WHERE ten_giang_vien = ?
				""";

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, tenGiangVien);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next())
					return Optional.of(GiangVienJdbcMapper.toDomain(rs));

				return Optional.empty();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new DataAccessException("Error finding GiangVien by ten", e);
		}
	}

	@Override
	public List<GiangVien> findAll()
	{
		String sql = """
				SELECT
				    id AS giang_vien_id,
				    ten_giang_vien
				FROM giang_vien
				ORDER BY ten_giang_vien ASC
				""";

		List<GiangVien> result = new ArrayList<>();

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while(rs.next()) {
				result.add(GiangVienJdbcMapper.toDomain(rs));
			}

			return result;
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding all GiangVien", e);
		}
	}

	@Override
	public void deleteById(Long id)
	{
		String sql = "DELETE FROM giang_vien WHERE id = ?";

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("GiangVien", id);
		}
		catch(SQLException e) {
			throw new DataAccessException("Error deleting GiangVien", e);
		}
	}
}