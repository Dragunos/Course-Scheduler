package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.out.VaiTroRepository;
import vn.edu.haui.scheduler.domain.model.VaiTro;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManagerImpl;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.VaiTroJdbcMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcVaiTroRepository implements VaiTroRepository
{
	private final TransactionManagerImpl transactionManager;

	public JdbcVaiTroRepository(TransactionManagerImpl transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	@Override
	public VaiTro save(VaiTro vaiTro)
	{
		if(vaiTro == null) {
			throw new ValidationException("VaiTro must not be null");
		}

		if(vaiTro.isPersisted()) {
			return update(vaiTro);
		}

		return insert(vaiTro);
	}

	private VaiTro insert(VaiTro vaiTro)
	{
		String sql = "INSERT INTO vai_tro (ten_vai_tro) VALUES (?)";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, vaiTro.getTenVaiTro());
			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) {
					Long id = rs.getLong(1);
					return VaiTro.reconstruct(id, vaiTro.getTenVaiTro());
				}
			}

			throw new DataAccessException("Failed to retrieve generated id for VaiTro", null);

		}
		catch(SQLException e) {
			throw new DataAccessException("Error inserting VaiTro", e);
		}
	}

	private VaiTro update(VaiTro vaiTro)
	{
		String sql = "UPDATE vai_tro SET ten_vai_tro = ? WHERE id = ?";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, vaiTro.getTenVaiTro());
			ps.setLong(2, vaiTro.getId());

			int affected = ps.executeUpdate();
			if(affected == 0) {
				throw new EntityNotFoundException("VaiTro", vaiTro.getId());
			}

			return vaiTro;

		}
		catch(SQLException e) {
			throw new DataAccessException("Error updating VaiTro", e);
		}
	}

	@Override
	public Optional<VaiTro> findById(Long id)
	{
		String sql = "SELECT id, ten_vai_tro FROM vai_tro WHERE id = ?";

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return Optional.of(VaiTroJdbcMapper.toDomain(rs));
				}
				return Optional.empty();
			}

		}
		catch(SQLException e) {
			throw new DataAccessException("Error finding VaiTro by id", e);
		}
	}

	@Override
	public Optional<VaiTro> findByTen(String tenVaiTro)
	{
		String sql = "SELECT id, ten_vai_tro FROM vai_tro WHERE ten_vai_tro = ?";

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, tenVaiTro);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return Optional.of(VaiTroJdbcMapper.toDomain(rs));
				}
				return Optional.empty();
			}

		}
		catch(SQLException e) {
			throw new DataAccessException("Error finding VaiTro by ten", e);
		}
	}

	@Override
	public List<VaiTro> findAll()
	{
		String sql = "SELECT id, ten_vai_tro FROM vai_tro";

		List<VaiTro> result = new ArrayList<>();

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while(rs.next()) {
				result.add(VaiTroJdbcMapper.toDomain(rs));
			}

			return result;

		}
		catch(SQLException e) {
			throw new DataAccessException("Error finding all VaiTro", e);
		}
	}

	@Override
	public void deleteById(Long id)
	{
		String sql = "DELETE FROM vai_tro WHERE id = ?";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);
			int affected = ps.executeUpdate();

			if(affected == 0) {
				throw new EntityNotFoundException("VaiTro", id);
			}

		}
		catch(SQLException e) {
			throw new DataAccessException("Error deleting VaiTro", e);
		}
	}
}