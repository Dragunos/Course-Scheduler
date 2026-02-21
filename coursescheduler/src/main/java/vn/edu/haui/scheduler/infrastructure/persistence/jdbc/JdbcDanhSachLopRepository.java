package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepository;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManagerImpl;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.DanhSachLopChiTietJdbcMapper;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.DanhSachLopJdbcMapper;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.HocKyJdbcMapper;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.LopHocPhanJdbcMapper;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.NguoiDungJdbcMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcDanhSachLopRepository implements DanhSachLopRepository
{
	private final TransactionManagerImpl transactionManager;

	public JdbcDanhSachLopRepository(TransactionManagerImpl transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	@Override
	public DanhSachLop save(DanhSachLop danhSachLop)
	{
		if(danhSachLop == null)
			throw new ValidationException("DanhSachLop must not be null");

		if(danhSachLop.getId() == null)
			return insert(danhSachLop);

		return update(danhSachLop);
	}

	private DanhSachLop insert(DanhSachLop dsl)
	{
		String sql = """
				INSERT INTO danh_sach_lop
				(ten_danh_sach, nguoi_tao_id, la_cong_khai, hoc_ky_id, ngay_tao)
				VALUES (?, ?, ?, ?, ?)
				""";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, dsl.getTenDanhSach());
			ps.setLong(2, dsl.getNguoiTao().getId());
			ps.setInt(3, dsl.isLaCongKhai() ? 1 : 0);

			if(dsl.getHocKy() != null)
				ps.setLong(4, dsl.getHocKy().getId());
			else
				ps.setNull(4, Types.BIGINT);

			ps.setTimestamp(5, Timestamp.valueOf(dsl.getNgayTao()));

			ps.executeUpdate();

			Long generatedId;
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(!rs.next())
					throw new DataAccessException("Failed to retrieve DanhSachLop id", null);
				generatedId = rs.getLong(1);
			}

			insertChiTiet(conn, generatedId, dsl.getChiTietList());

			return DanhSachLop.reconstruct(
					generatedId,
					dsl.getTenDanhSach(),
					dsl.getNguoiTao(),
					dsl.isLaCongKhai(),
					dsl.getHocKy(),
					dsl.getNgayTao(),
					dsl.getChiTietList());
		}
		catch(SQLException e) {
			throw new DataAccessException("Error inserting DanhSachLop", e);
		}
	}

	private DanhSachLop update(DanhSachLop dsl)
	{
		String sql = """
				UPDATE danh_sach_lop
				SET ten_danh_sach = ?, la_cong_khai = ?, hoc_ky_id = ?
				WHERE id = ?
				""";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, dsl.getTenDanhSach());
			ps.setInt(2, dsl.isLaCongKhai() ? 1 : 0);

			if(dsl.getHocKy() != null)
				ps.setLong(3, dsl.getHocKy().getId());
			else
				ps.setNull(3, Types.BIGINT);

			ps.setLong(4, dsl.getId());

			int affected = ps.executeUpdate();
			if(affected == 0)
				throw new EntityNotFoundException("DanhSachLop", dsl.getId());

			deleteAllChiTiet(conn, dsl.getId());
			insertChiTiet(conn, dsl.getId(), dsl.getChiTietList());

			return dsl;
		}
		catch(SQLException e) {
			throw new DataAccessException("Error updating DanhSachLop", e);
		}
	}

	@Override
	public Optional<DanhSachLop> findById(Long id)
	{
		String sql = """
				SELECT dsl.*, nd.*, hk.*
				FROM danh_sach_lop dsl
				JOIN nguoi_dung nd ON dsl.nguoi_tao_id = nd.id
				LEFT JOIN hoc_ky hk ON dsl.hoc_ky_id = hk.id
				WHERE dsl.id = ?
				""";

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(!rs.next())
					return Optional.empty();

				return Optional.of(loadAggregate(conn, rs));
			}
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding DanhSachLop by id", e);
		}
	}

	@Override
	public List<DanhSachLop> findByNguoiTaoId(Long nguoiTaoId)
	{
		String sql = "SELECT id FROM danh_sach_lop WHERE nguoi_tao_id = ?";

		return findByQuery(sql, nguoiTaoId);
	}

	@Override
	public List<DanhSachLop> findPublicLists()
	{
		String sql = "SELECT id FROM danh_sach_lop WHERE la_cong_khai = 1";
		return findByQuery(sql, null);
	}

	private List<DanhSachLop> findByQuery(String sql, Long param)
	{
		List<DanhSachLop> result = new ArrayList<>();

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			if(param != null)
				ps.setLong(1, param);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					findById(rs.getLong("id")).ifPresent(result::add);
				}
			}
		}
		catch(Exception e) {
			throw new DataAccessException("Error querying DanhSachLop", e);
		}

		return result;
	}

	@Override
	public void deleteById(Long id)
	{
		String sql = "DELETE FROM danh_sach_lop WHERE id = ?";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			int affected = ps.executeUpdate();
			if(affected == 0)
				throw new EntityNotFoundException("DanhSachLop", id);
		}
		catch(SQLException e) {
			throw new DataAccessException("Error deleting DanhSachLop", e);
		}
	}

	private DanhSachLop loadAggregate(Connection conn, ResultSet rs) throws Exception
	{
		Long id = rs.getLong("id");

		NguoiDung nguoiTao = NguoiDungJdbcMapper.toDomain(rs);

		HocKy hocKy = null;
		if(rs.getObject("hoc_ky_id") != null) {
			hocKy = HocKyJdbcMapper.toDomain(rs);
		}

		List<DanhSachLopChiTiet> chiTiet = loadChiTiet(conn, id);

		return DanhSachLopJdbcMapper.toDomain(rs, nguoiTao, hocKy, chiTiet);
	}

	private List<DanhSachLopChiTiet> loadChiTiet(Connection conn, Long danhSachId)
			throws Exception
	{
		String sql = """
				SELECT
				    ct.danh_sach_lop_id,
				    ct.lop_hoc_phan_id,
				    ct.bat_buoc,
				    lhp.id AS lhp_id,
				    lhp.ma_lop,
				    lhp.hoc_phan_id,
				    lhp.giang_vien_id,
				    lhp.hinh_thuc_day,
				    lhp.dia_diem
				FROM danh_sach_lop_chi_tiet ct
				JOIN lop_hoc_phan lhp ON ct.lop_hoc_phan_id = lhp.id
				WHERE ct.danh_sach_lop_id = ?
							""";

		List<DanhSachLopChiTiet> result = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, danhSachId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {

					LopHocPhan lop = LopHocPhanJdbcMapper.toDomain(rs);

					DanhSachLopChiTiet chiTiet = DanhSachLopChiTietJdbcMapper.toDomain(rs, lop);

					result.add(chiTiet);
				}
			}
		}

		return result;
	}

	private void insertChiTiet(Connection conn,
			Long danhSachId,
			List<DanhSachLopChiTiet> list)
			throws SQLException
	{
		if(list == null || list.isEmpty())
			return;

		String sql = """
				INSERT INTO danh_sach_lop_chi_tiet
				(danh_sach_lop_id, lop_hoc_phan_id, bat_buoc)
				VALUES (?, ?, ?)
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			for(DanhSachLopChiTiet ct : list) {
				ps.setLong(1, danhSachId);
				ps.setLong(2, ct.getLopHocPhan().getId());
				ps.setInt(3, ct.isBatBuoc() ? 1 : 0);
				ps.addBatch();
			}

			ps.executeBatch();
		}
	}

	private void deleteAllChiTiet(Connection conn, Long danhSachId)
			throws SQLException
	{
		String sql = "DELETE FROM danh_sach_lop_chi_tiet WHERE danh_sach_lop_id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, danhSachId);
			ps.executeUpdate();
		}
	}
}