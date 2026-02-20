package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.NguoiDungRepository;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManagerImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Optional;

public class JdbcNguoiDungRepository implements NguoiDungRepository
{
	private final TransactionManagerImpl txManager;

	public JdbcNguoiDungRepository(TransactionManagerImpl txManager)
	{
		this.txManager = txManager;
	}

	@Override
	public Optional<NguoiDung> findByTenDangNhap(String tenDangNhap) throws DataAccessException
	{
		String sql = """
				SELECT id, ten_dang_nhap, mat_khau_hash, role_id, ngay_tao
				FROM nguoi_dung
				WHERE ten_dang_nhap = ?
				""";

		Connection connection = txManager.getExistingConnection();
		boolean isNewConnection = false;

		try {

			if(connection == null) {
				connection = DataSourceProvider.getConnection();
				isNewConnection = true;
			}

			try (PreparedStatement ps = connection.prepareStatement(sql)) {

				ps.setString(1, tenDangNhap);

				try (ResultSet rs = ps.executeQuery()) {

					if(rs.next()) {

						Object roleObj = rs.getObject("role_id");
						Long vaiTroId = roleObj == null ? null : ((Number) roleObj).longValue();

						Timestamp ts = rs.getTimestamp("ngay_tao");
						LocalDateTime ngayTao = ts == null ? null : ts.toLocalDateTime();

						return Optional.of(new NguoiDung(
								rs.getLong("id"),
								rs.getString("ten_dang_nhap"),
								rs.getString("mat_khau_hash"),
								vaiTroId,
								ngayTao));
					}

					return Optional.empty();
				}
			}
		}
		catch(Exception ex) {
			throw new DataAccessException("Error when finding nguoi_dung", ex);
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
	public long save(NguoiDung user) throws DataAccessException
	{
		String sql = """
				INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau_hash, role_id)
				VALUES (?, ?, ?)
				""";

		try {

			Connection connection = txManager.getRequiredConnection();

			try (
					PreparedStatement ps = connection.prepareStatement(
							sql,
							Statement.RETURN_GENERATED_KEYS)) {

				ps.setString(1, user.getTenDangNhap());
				ps.setString(2, user.getMatKhauHash());

				if(user.getVaiTroId() == null)
					ps.setNull(3, Types.BIGINT);
				else
					ps.setObject(3, user.getVaiTroId(), Types.BIGINT);

				int affected = ps.executeUpdate();

				if(affected == 0)
					throw new DataAccessException("Insert failed");

				try (ResultSet keys = ps.getGeneratedKeys()) {

					if(keys.next())
						return keys.getLong(1);
				}

				throw new DataAccessException("No ID returned");
			}
		}
		catch(Exception ex) {
			throw new DataAccessException("Error when saving nguoi_dung", ex);
		}
	}
}
