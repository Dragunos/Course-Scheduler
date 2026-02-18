package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.exception.PersistenceException;
import vn.edu.haui.scheduler.application.port.out.HocPhanRepositoryPort;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.util.Optional;

public class JdbcHocPhanRepository implements HocPhanRepositoryPort
{
	@Override
	public Optional<Long> findIdByMaHocPhan(String maHocPhan)
	{
		if(maHocPhan == null) return Optional.empty();

		String sql = "SELECT id FROM hoc_phan WHERE ma_hoc_phan = ?";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, maHocPhan);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) return Optional.of(rs.getLong(1));
			}

			return Optional.empty();

		}
		catch(SQLException ex) {
			throw new PersistenceException("Failed to find HocPhan by maHocPhan", ex);
		}
	}

	@Override
	public Long save(HocPhan hocPhan)
	{
		String insert = """
				INSERT INTO hoc_phan (ma_hoc_phan, ten_hoc_phan, so_tin_chi)
				VALUES (?, ?, ?)
				""";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, hocPhan.getMaHocPhan());
			ps.setString(2, hocPhan.getTenHocPhan());

			if(hocPhan.getSoTinChi() != null)
				ps.setInt(3, hocPhan.getSoTinChi());
			else
				ps.setNull(3, Types.INTEGER);

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new PersistenceException("Insert hoc_phan failed");

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) return rs.getLong(1);
			}

			throw new PersistenceException("No ID returned when saving HocPhan");

		}
		catch(SQLException ex) {
			throw new PersistenceException("Failed to save HocPhan", ex);
		}
	}
}
