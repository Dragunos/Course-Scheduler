package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.out.HocKyRepository;
import vn.edu.haui.scheduler.domain.model.HocKy;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManagerImpl;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.HocKyJdbcMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcHocKyRepository implements HocKyRepository
{
	private final TransactionManagerImpl transactionManager;

	public JdbcHocKyRepository(TransactionManagerImpl transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	@Override
	public HocKy save(HocKy hocKy)
	{
		if(hocKy == null)
			throw new ValidationException("HocKy must not be null");

		if(hocKy.isPersisted())
			return update(hocKy);

		return insert(hocKy);
	}

	private HocKy insert(HocKy hocKy)
	{
		String sql = """
				INSERT INTO hoc_ky (ten_hoc_ky, nam_hoc)
				VALUES (?, ?)
				""";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, hocKy.getTenHocKy());
			ps.setString(2, hocKy.getNamHoc());

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) {
					Long id = rs.getLong(1);
					return HocKy.reconstruct(
							id,
							hocKy.getTenHocKy(),
							hocKy.getNamHoc());
				}
			}

			throw new DataAccessException("Failed to retrieve generated id for HocKy", null);
		}
		catch(SQLException e) {
			throw new DataAccessException("Error inserting HocKy", e);
		}
	}

	private HocKy update(HocKy hocKy)
	{
		String sql = """
				UPDATE hoc_ky
				SET ten_hoc_ky = ?, nam_hoc = ?
				WHERE id = ?
				""";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, hocKy.getTenHocKy());
			ps.setString(2, hocKy.getNamHoc());
			ps.setLong(3, hocKy.getId());

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("HocKy", hocKy.getId());

			return hocKy;
		}
		catch(SQLException e) {
			throw new DataAccessException("Error updating HocKy", e);
		}
	}

	@Override
	public Optional<HocKy> findById(Long id)
	{
		String sql = """
				SELECT id, ten_hoc_ky, nam_hoc
				FROM hoc_ky
				WHERE id = ?
				""";

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next())
					return Optional.of(HocKyJdbcMapper.toDomain(rs));

				return Optional.empty();
			}
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding HocKy by id", e);
		}
	}

	@Override
	public Optional<HocKy> findByTenAndNam(String tenHocKy, String namHoc)
	{
		String sql = """
				SELECT id, ten_hoc_ky, nam_hoc
				FROM hoc_ky
				WHERE ten_hoc_ky = ? AND nam_hoc = ?
				""";

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, tenHocKy);
			ps.setString(2, namHoc);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next())
					return Optional.of(HocKyJdbcMapper.toDomain(rs));

				return Optional.empty();
			}
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding HocKy by ten and nam", e);
		}
	}

	@Override
	public List<HocKy> findAll()
	{
		String sql = """
				SELECT id, ten_hoc_ky, nam_hoc
				FROM hoc_ky
				ORDER BY nam_hoc DESC, ten_hoc_ky DESC
				""";

		List<HocKy> result = new ArrayList<>();

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while(rs.next()) {
				result.add(HocKyJdbcMapper.toDomain(rs));
			}

			return result;
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding all HocKy", e);
		}
	}

	@Override
	public void deleteById(Long id)
	{
		String sql = "DELETE FROM hoc_ky WHERE id = ?";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("HocKy", id);
		}
		catch(SQLException e) {
			throw new DataAccessException("Error deleting HocKy", e);
		}
	}
}