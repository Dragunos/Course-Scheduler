package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.out.LopHocPhanRepository;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLopHocPhanRepository implements LopHocPhanRepository
{
	private static final String BASE_SELECT_QUERY = """
			SELECT lhp.id as lhp_id,
			       lhp.ma_lop as lhp_ma_lop,
			       lhp.hinh_thuc_day,
			       lhp.dia_diem,
			       hp.id AS hoc_phan_id,
			       hp.ma_hoc_phan,
			       hp.ten_hoc_phan,
			       hp.so_tin_chi,
			       gv.id AS giang_vien_id,
			       gv.ten_giang_vien
			FROM lop_hoc_phan lhp
			JOIN hoc_phan hp ON lhp.hoc_phan_id = hp.id
			LEFT JOIN giang_vien gv ON lhp.giang_vien_id = gv.id
			""";

	public JdbcLopHocPhanRepository()
	{
	}

	@Override
	public LopHocPhan save(LopHocPhan lopHocPhan)
	{
		if(lopHocPhan == null)
			throw new ValidationException("LopHocPhan must not be null");

		if(lopHocPhan.getId() == null)
			return insert(lopHocPhan);

		return update(lopHocPhan);
	}

	private LopHocPhan insert(LopHocPhan lopHocPhan)
	{
		String sql = """
				INSERT INTO lop_hoc_phan
				(ma_lop, hoc_phan_id, giang_vien_id, hinh_thuc_day, dia_diem)
				VALUES (?, ?, ?, ?, ?)
				""";

		return executeWrite(sql, true, ps -> {

			ps.setString(1, lopHocPhan.getMaLop());
			ps.setLong(2, lopHocPhan.getHocPhan().getId());

			if(lopHocPhan.getGiangVien() != null)
				ps.setLong(3, lopHocPhan.getGiangVien().getId());
			else
				ps.setNull(3, Types.BIGINT);

			ps.setString(4, lopHocPhan.getHinhThucDay());
			ps.setString(5, lopHocPhan.getDiaDiem());

			ps.executeUpdate();

			Long generatedId;

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(!rs.next())
					throw new DataAccessException("Failed to retrieve LopHocPhan id", null);

				generatedId = rs.getLong(1);
			}

			insertLichHoc(ps.getConnection(), generatedId, lopHocPhan.getLichHocList());

			return LopHocPhan.reconstruct(
					generatedId,
					lopHocPhan.getMaLop(),
					lopHocPhan.getHocPhan(),
					lopHocPhan.getGiangVien(),
					lopHocPhan.getHinhThucDay(),
					lopHocPhan.getDiaDiem(),
					lopHocPhan.getLichHocList());
		});
	}

	private LopHocPhan update(LopHocPhan lopHocPhan)
	{
		String sql = """
				UPDATE lop_hoc_phan
				SET ma_lop = ?, hoc_phan_id = ?, giang_vien_id = ?,
				    hinh_thuc_day = ?, dia_diem = ?
				WHERE id = ?
				""";

		return executeWrite(sql, false, ps -> {

			ps.setString(1, lopHocPhan.getMaLop());
			ps.setLong(2, lopHocPhan.getHocPhan().getId());

			if(lopHocPhan.getGiangVien() != null)
				ps.setLong(3, lopHocPhan.getGiangVien().getId());
			else
				ps.setNull(3, Types.BIGINT);

			ps.setString(4, lopHocPhan.getHinhThucDay());
			ps.setString(5, lopHocPhan.getDiaDiem());
			ps.setLong(6, lopHocPhan.getId());

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("LopHocPhan", lopHocPhan.getId());

			deleteAllLichHoc(ps.getConnection(), lopHocPhan.getId());
			insertLichHoc(ps.getConnection(), lopHocPhan.getId(), lopHocPhan.getLichHocList());

			return lopHocPhan;
		});
	}

	@Override
	public Optional<LopHocPhan> findById(Long id)
	{
		String sql = buildQuery("WHERE lhp.id = ?");

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(!rs.next())
					return Optional.empty();

				return Optional.of(loadAggregate(conn, rs));
			}
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding LopHocPhan by id", e);
		}
	}

	@Override
	public List<LopHocPhan> findByHocPhanId(Long hocPhanId)
	{
		String sql = "SELECT id FROM lop_hoc_phan WHERE hoc_phan_id = ?";

		List<LopHocPhan> result = new ArrayList<>();

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, hocPhanId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					findById(rs.getLong("id"))
							.ifPresent(result::add);
				}
			}

			return result;
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding LopHocPhan by HocPhanId", e);
		}
	}

	@Override
	public List<LopHocPhan> findAll()
	{
		String sql = "SELECT id FROM lop_hoc_phan";

		List<LopHocPhan> result = new ArrayList<>();

		try (Connection conn = DataSourceProvider.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while(rs.next()) {
				findById(rs.getLong("id"))
						.ifPresent(result::add);
			}

			return result;
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding all LopHocPhan", e);
		}
	}

	@Override
	public void deleteById(Long id)
	{
		String sql = "DELETE FROM lop_hoc_phan WHERE id = ?";

		executeWrite(sql, false, ps -> {

			ps.setLong(1, id);

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("LopHocPhan", id);

			return null;
		});
	}

	private LopHocPhan loadAggregate(Connection conn, ResultSet rs) throws Exception
	{
		// use aliases declared in BASE_SELECT_QUERY
		Long id = rs.getLong("lhp_id"); // was "id"
		String maLop = rs.getString("lhp_ma_lop"); // was "ma_lop"

		HocPhan hocPhan = HocPhanJdbcMapper.toDomain(rs); // update mapper to read hoc_phan_id
		GiangVien giangVien = null;
		if(rs.getObject("giang_vien_id") != null)
			giangVien = GiangVienJdbcMapper.toDomain(rs); // update mapper to read giang_vien_id

		List<LichHoc> lichHocList = loadLichHoc(conn, id);

		return LopHocPhan.reconstruct(
				id,
				maLop,
				hocPhan,
				giangVien,
				rs.getString("hinh_thuc_day"),
				rs.getString("dia_diem"),
				lichHocList);
	}

	private List<LichHoc> loadLichHoc(Connection conn, Long lopHocPhanId) throws Exception
	{
		System.out.println("DEBUG: loadLichHoc for lopHocPhanId=" + lopHocPhanId);
		String sql = "SELECT * FROM lich_hoc WHERE lop_hoc_phan_id = ?";

		List<LichHoc> result = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, lopHocPhanId);

			try (ResultSet rs = ps.executeQuery()) {

				while(rs.next()) {
					System.out.println("DEBUG: found lichHoc row id=" + rs.getLong("id"));
					result.add(LichHocJdbcMapper.toDomain(rs));
				}
			}
		}

		return result;
	}

	private void insertLichHoc(Connection conn, Long lopHocPhanId, List<LichHoc> lichHocList)
	{
		if(lichHocList == null || lichHocList.isEmpty())
			return;

		String sql = """
				INSERT INTO lich_hoc
				(lop_hoc_phan_id, thu, tiet_bat_dau, tiet_ket_thuc)
				VALUES (?, ?, ?, ?)
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			for(LichHoc lich : lichHocList) {

				ps.setLong(1, lopHocPhanId);
				ps.setInt(2, lich.getThu());
				ps.setInt(3, lich.getTietBatDau());
				ps.setInt(4, lich.getTietKetThuc());

				ps.addBatch();
			}

			ps.executeBatch();

		}
		catch(SQLException e) {
			throw new DataAccessException(
					"Error inserting LichHoc",
					e);
		}
	}

	private void deleteAllLichHoc(Connection conn, Long lopHocPhanId)
	{
		String sql = "DELETE FROM lich_hoc WHERE lop_hoc_phan_id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, lopHocPhanId);
			ps.executeUpdate();

		}
		catch(SQLException e) {
			throw new DataAccessException(
					"Error deleting LichHoc of LopHocPhan",
					e);
		}
	}

	private <T> T executeWrite(
			String sql,
			boolean returnGeneratedKeys,
			SqlFunction<PreparedStatement, T> executor)
	{
		try (Connection conn = DataSourceProvider.getConnection()) {
			conn.setAutoCommit(false);

			try (PreparedStatement ps = conn.prepareStatement(
					sql,
					returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS)) {
				T result = executor.apply(ps);

				conn.commit();

				return result;
			}
			catch(Exception ex) {
				try {
					conn.rollback();
				}
				catch(SQLException rollbackEx) {
					ex.addSuppressed(rollbackEx);
				}
				throw ex;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new DataAccessException("Database write operation failed", e);
		}
	}

	@FunctionalInterface
	private interface SqlFunction<T, R>
	{
		R apply(T t) throws SQLException;
	}

	private String buildQuery(String whereClause)
	{
		if(whereClause == null || whereClause.isBlank())
			return BASE_SELECT_QUERY;

		return BASE_SELECT_QUERY.strip() + " " + whereClause.strip();
	}
}