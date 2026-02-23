package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.out.HocPhanRepository;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.HocPhanJdbcMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcHocPhanRepository implements HocPhanRepository
{
	public JdbcHocPhanRepository()
	{
	}

	@Override
	public HocPhan save(HocPhan hocPhan)
	{
		if(hocPhan == null)
			throw new ValidationException("HocPhan must not be null");

		if(hocPhan.isPersisted())
			return update(hocPhan);

		return insert(hocPhan);
	}

	private HocPhan insert(HocPhan hocPhan)
	{
		String sql = """
				INSERT INTO hoc_phan
				(ma_hoc_phan, ten_hoc_phan, so_tin_chi)
				VALUES (?, ?, ?)
				""";

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, hocPhan.getMaHocPhan());
			ps.setString(2, hocPhan.getTenHocPhan());
			ps.setInt(3, hocPhan.getSoTinChi());

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) {
					Long id = rs.getLong(1);
					return HocPhan.reconstruct(
							id,
							hocPhan.getMaHocPhan(),
							hocPhan.getTenHocPhan(),
							hocPhan.getSoTinChi());
				}
			}

			throw new DataAccessException("Failed to retrieve generated id for HocPhan", null);
		}
		catch(SQLException e) {
			throw new DataAccessException("Error inserting HocPhan", e);
		}
	}

	private HocPhan update(HocPhan hocPhan)
	{
		String sql = """
				UPDATE hoc_phan
				SET ten_hoc_phan = ?, so_tin_chi = ?
				WHERE id = ?
				""";

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, hocPhan.getTenHocPhan());
			ps.setInt(2, hocPhan.getSoTinChi());
			ps.setLong(3, hocPhan.getId());

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("HocPhan", hocPhan.getId());

			return hocPhan;
		}
		catch(SQLException e) {
			throw new DataAccessException("Error updating HocPhan", e);
		}
	}

	@Override
	public Optional<HocPhan> findById(Long id)
	{
		String sql = """
				SELECT id, ma_hoc_phan, ten_hoc_phan, so_tin_chi
				FROM hoc_phan
				WHERE id = ?
				""";

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next())
					return Optional.of(HocPhanJdbcMapper.toDomain(rs));

				return Optional.empty();
			}
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding HocPhan by id", e);
		}
	}

	@Override
	public Optional<HocPhan> findByMaHocPhan(String maHocPhan)
	{
		String sql = """
				SELECT id, ma_hoc_phan, ten_hoc_phan, so_tin_chi
				FROM hoc_phan
				WHERE ma_hoc_phan = ?
				""";

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, maHocPhan);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next())
					return Optional.of(HocPhanJdbcMapper.toDomain(rs));

				return Optional.empty();
			}
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding HocPhan by maHocPhan", e);
		}
	}

	@Override
	public List<HocPhan> findAll()
	{
		String sql = """
				SELECT id, ma_hoc_phan, ten_hoc_phan, so_tin_chi
				FROM hoc_phan
				ORDER BY ma_hoc_phan ASC
				""";

		List<HocPhan> result = new ArrayList<>();

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while(rs.next()) {
				result.add(HocPhanJdbcMapper.toDomain(rs));
			}

			return result;
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding all HocPhan", e);
		}
	}

	@Override
	public void deleteById(Long id)
	{
		String sql = "DELETE FROM hoc_phan WHERE id = ?";

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("HocPhan", id);
		}
		catch(SQLException e) {
			throw new DataAccessException("Error deleting HocPhan", e);
		}
	}
}