package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.VaiTroRepositoryPort;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class JdbcVaiTroRepository implements VaiTroRepositoryPort
{

	@Override
	public Optional<Integer> findIdByName(String roleName) throws Exception
	{
		String sql = "SELECT id FROM vai_tro WHERE ten_vai_tro = ?";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, roleName);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return Optional.of(rs.getInt("id"));
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
