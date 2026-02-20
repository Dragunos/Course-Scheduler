package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.VaiTroRepository;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManagerImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class JdbcVaiTroRepository implements VaiTroRepository
{
	private final TransactionManagerImpl txManager;

	public JdbcVaiTroRepository(TransactionManagerImpl txManager)
	{
		this.txManager = txManager;
	}

	@Override
	public Optional<Long> findIdByTenVaiTro(String tenVaiTro) throws DataAccessException
	{

		String sql = "SELECT id FROM vai_tro WHERE ten_vai_tro = ?";

		Connection connection = txManager.getExistingConnection();
		boolean isNewConnection = false;

		try {

			if(connection == null) {
				connection = DataSourceProvider.getConnection();
				isNewConnection = true;
			}

			try (PreparedStatement ps = connection.prepareStatement(sql)) {

				ps.setString(1, tenVaiTro);

				try (ResultSet rs = ps.executeQuery()) {
					if(rs.next())
						return Optional.of(rs.getLong("id"));

					return Optional.empty();
				}
			}
		}
		catch(Exception ex) {
			throw new DataAccessException("Error when finding vai_tro id", ex);
		}
		finally {
			if(isNewConnection) {
				try {
					connection.close();
				}
				catch(Exception ignored) {
				}
			}
		}
	}

	@Override
	public Optional<String> findTenById(Long id) throws DataAccessException
	{

		String sql = "SELECT ten_vai_tro FROM vai_tro WHERE id = ?";

		Connection connection = txManager.getExistingConnection();
		boolean isNewConnection = false;

		try {

			if(connection == null) {
				connection = DataSourceProvider.getConnection();
				isNewConnection = true;
			}

			try (PreparedStatement ps = connection.prepareStatement(sql)) {

				ps.setLong(1, id);

				try (ResultSet rs = ps.executeQuery()) {
					if(rs.next())
						return Optional.of(rs.getString("ten_vai_tro"));

					return Optional.empty();
				}
			}
		}
		catch(Exception ex) {
			throw new DataAccessException("Error when finding ten_vai_tro", ex);
		}
		finally {
			if(isNewConnection) {
				try {
					connection.close();
				}
				catch(Exception ignored) {
				}
			}
		}
	}

	@Override
	public long save(String tenVaiTro) throws DataAccessException
	{
		String sql = "INSERT INTO vai_tro (ten_vai_tro) VALUES (?)";

		try {

			Connection connection = txManager.getRequiredConnection();

			try (
					PreparedStatement ps = connection.prepareStatement(
							sql,
							Statement.RETURN_GENERATED_KEYS)) {

				ps.setString(1, tenVaiTro);

				int affected = ps.executeUpdate();

				if(affected == 0)
					throw new DataAccessException("Insert role failed");

				try (ResultSet keys = ps.getGeneratedKeys()) {

					if(keys.next())
						return keys.getLong(1);
				}

				throw new DataAccessException("No ID returned");
			}
		}
		catch(Exception ex) {
			throw new DataAccessException("Error when saving vai_tro", ex);
		}
	}
}
