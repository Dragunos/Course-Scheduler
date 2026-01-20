package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

import vn.edu.haui.scheduler.infrastructure.persistence.VaiTroRepository;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

public class JdbcVaiTroRepository implements VaiTroRepository
{

	@Override
	public Optional<Integer> timIdTheoTenVaiTro(String tenVaiTro)
	{

		String sql = "SELECT id FROM vai_tro WHERE ten_vai_tro = ?";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, tenVaiTro);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return Optional.of(rs.getInt("id"));
				}
			}

			return Optional.empty();
		}
		catch(Exception e) {
			throw new RuntimeException("Lỗi khi tìm vai trò", e);
		}
	}

	@Override
	public long luu(String tenVaiTro) throws Exception
	{

		String sql = "INSERT INTO vai_tro (ten_vai_tro) VALUES (?)";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, tenVaiTro);

			int affected = ps.executeUpdate();
			if(affected == 0) {
				throw new RuntimeException("Insert role failed, no rows affected");
			}

			try (ResultSet keys = ps.getGeneratedKeys()) {
				if(keys.next()) {
					return keys.getLong(1);
				}
				else {
					throw new RuntimeException("Insert role failed, no ID obtained");
				}
			}
		}
	}
}
