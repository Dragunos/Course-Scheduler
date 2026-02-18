package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.VaiTroRepository;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class JdbcVaiTroRepository implements VaiTroRepository
{
	@Override
	public Optional<Long> findIdByName(String roleName) throws Exception
	{
		String sql = "SELECT id FROM vai_tro WHERE ten_vai_tro = ?";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, roleName);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return Optional.of(rs.getLong("id"));
				}
			}
			return Optional.empty();
		}
	}

	@Override
	public Optional<String> findNameById(Long id) throws Exception
	{
		String sql = "SELECT ten_vai_tro FROM vai_tro WHERE id = ?";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return Optional.of(rs.getString("ten_vai_tro"));
				}
			}
			return Optional.empty();
		}
	}

	@Override
	public long save(String roleName) throws Exception
	{
		String sql = "INSERT INTO vai_tro (ten_vai_tro) VALUES (?)";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, roleName);

			int affected = ps.executeUpdate();
			if(affected == 0) {
				throw new RuntimeException("Insert role failed");
			}

			try (ResultSet keys = ps.getGeneratedKeys()) {
				if(keys.next()) {
					return keys.getLong(1);
				}
				throw new RuntimeException("No ID returned");
			}
		}
	}
}
