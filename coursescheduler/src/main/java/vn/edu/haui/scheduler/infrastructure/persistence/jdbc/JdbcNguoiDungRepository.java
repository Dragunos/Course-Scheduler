package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.out.NguoiDungRepository;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManagerImpl;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.NguoiDungJdbcMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcNguoiDungRepository implements NguoiDungRepository
{
	private final TransactionManagerImpl transactionManager;

	private static final String BASE_SELECT_QUERY = """
			SELECT nd.id,
			       nd.ten_dang_nhap,
			       nd.mat_khau_hash,
			       nd.ngay_tao,
			       vt.id AS vai_tro_id,
			       vt.ten_vai_tro
			FROM nguoi_dung nd
			LEFT JOIN vai_tro vt ON nd.role_id = vt.id
			""";

	public JdbcNguoiDungRepository(TransactionManagerImpl transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	@Override
	public NguoiDung save(NguoiDung nguoiDung)
	{
		if(nguoiDung == null) {
			throw new ValidationException("NguoiDung must not be null");
		}

		if(nguoiDung.isPersisted()) {
			return update(nguoiDung);
		}

		return insert(nguoiDung);
	}

	private NguoiDung insert(NguoiDung nguoiDung)
	{
		String sql = """
				INSERT INTO nguoi_dung
				(ten_dang_nhap, mat_khau_hash, role_id, ngay_tao)
				VALUES (?, ?, ?, ?)
				""";

		return executeWrite(sql, true, ps -> {

			ps.setString(1, nguoiDung.getTenDangNhap());
			ps.setString(2, nguoiDung.getMatKhauHash());
			ps.setLong(3, nguoiDung.getVaiTro().getId());
			ps.setTimestamp(4,
					Timestamp.valueOf(nguoiDung.getNgayTao()));

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) {
					Long id = rs.getLong(1);

					return NguoiDung.reconstruct(
							id,
							nguoiDung.getTenDangNhap(),
							nguoiDung.getMatKhauHash(),
							nguoiDung.getVaiTro(),
							nguoiDung.getNgayTao());
				}
			}

			throw new DataAccessException(
					"Failed to retrieve generated id", null);
		});
	}

	private NguoiDung update(NguoiDung nguoiDung)
	{
		String sql = """
				UPDATE nguoi_dung
				SET mat_khau_hash = ?,
				    role_id = ?
				WHERE id = ?
				""";

		return executeWrite(sql, false, ps -> {

			ps.setString(1, nguoiDung.getMatKhauHash());
			ps.setLong(2, nguoiDung.getVaiTro().getId());
			ps.setLong(3, nguoiDung.getId());

			int affected = ps.executeUpdate();

			if(affected == 0) {
				throw new EntityNotFoundException(
						"NguoiDung",
						nguoiDung.getId());
			}

			return nguoiDung;
		});
	}

	@Override
	public Optional<NguoiDung> findById(Long id)
	{
		String sql = buildQuery("WHERE nd.id = ?");

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return Optional.of(NguoiDungJdbcMapper.toDomain(rs));
				}
				return Optional.empty();
			}
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding NguoiDung by id", e);
		}
	}

	@Override
	public Optional<NguoiDung> findByUsername(String username)
	{
		String sql = buildQuery("WHERE nd.ten_dang_nhap = ?");

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, username);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return Optional.of(NguoiDungJdbcMapper.toDomain(rs));
				}
				return Optional.empty();
			}
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding NguoiDung by username", e);
		}
	}

	@Override
	public List<NguoiDung> findAll()
	{
		String sql = buildQuery(null);

		List<NguoiDung> result = new ArrayList<>();

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while(rs.next()) {
				result.add(NguoiDungJdbcMapper.toDomain(rs));
			}

			return result;
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding all NguoiDung", e);
		}
	}

	@Override
	public void deleteById(Long id)
	{
		String sql = "DELETE FROM nguoi_dung WHERE id = ?";

		executeWrite(sql, false, ps -> {

			ps.setLong(1, id);

			int affected = ps.executeUpdate();

			if(affected == 0) {
				throw new EntityNotFoundException("NguoiDung", id);
			}

			return null;
		});
	}

	@Override
	public boolean existsByUsername(String username)
	{
		String sql = "SELECT 1 FROM nguoi_dung WHERE ten_dang_nhap = ?";

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, username);

			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}

		}
		catch(SQLException e) {
			throw new DataAccessException("Error checking username existence", e);
		}
	}

	private <T> T executeWrite(
			String sql,
			boolean returnGeneratedKeys,
			SqlFunction<PreparedStatement, T> executor)
	{
		Connection conn = transactionManager.getRequiredConnection();

		try (PreparedStatement ps = conn.prepareStatement(
				sql,
				returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS)) {

			return executor.apply(ps);

		}
		catch(SQLException e) {
			throw new DataAccessException("Database write operation failed", e);
		}
	}

	private String buildQuery(String whereClause)
	{
		if(whereClause == null || whereClause.isBlank()) {
			return BASE_SELECT_QUERY;
		}

		return BASE_SELECT_QUERY.strip() +
				(whereClause == null || whereClause.isBlank()
						? ""
						: " " + whereClause.strip());
	}

	@FunctionalInterface
	private interface SqlFunction<T, R>
	{
		R apply(T t) throws SQLException;
	}
}