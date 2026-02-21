package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.out.YeuCauRepository;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManagerImpl;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.*;

import java.sql.*;
import java.util.*;

public class JdbcYeuCauRepository implements YeuCauRepository
{
	private final TransactionManagerImpl transactionManager;

	public JdbcYeuCauRepository(TransactionManagerImpl transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	@Override
	public YeuCau save(YeuCau yeuCau)
	{
		if(yeuCau == null)
			throw new ValidationException("YeuCau must not be null");

		if(yeuCau.getId() == null)
			return insert(yeuCau);

		return update(yeuCau);
	}

	private YeuCau insert(YeuCau yeuCau)
	{
		String sql = """
				INSERT INTO yeu_cau
				(nguoi_tao_id, danh_sach_lop_id, ten_yeu_cau, ngay_tao)
				VALUES (?, ?, ?, ?)
				""";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setLong(1, yeuCau.getNguoiTao().getId());
			ps.setLong(2, yeuCau.getDanhSachLop().getId());
			ps.setString(3, yeuCau.getTenYeuCau());
			ps.setTimestamp(4, Timestamp.valueOf(yeuCau.getNgayTao()));

			ps.executeUpdate();

			Long generatedId;

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(!rs.next())
					throw new DataAccessException("Failed to retrieve YeuCau id", null);
				generatedId = rs.getLong(1);
			}

			insertChiTiet(conn, generatedId, yeuCau.getChiTiet());
			insertRangBuoc(conn, generatedId, yeuCau.getRangBuoc());

			return findById(generatedId)
					.orElseThrow(() -> new DataAccessException("Failed to reload YeuCau", null));

		}
		catch(SQLException e) {
			throw new DataAccessException("Error inserting YeuCau", e);
		}
	}

	private YeuCau update(YeuCau yeuCau)
	{
		String sql = """
				UPDATE yeu_cau
				SET ten_yeu_cau = ?
				WHERE id = ?
				""";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, yeuCau.getTenYeuCau());
			ps.setLong(2, yeuCau.getId());

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("YeuCau", yeuCau.getId());

			deleteAllChiTiet(conn, yeuCau.getId());
			deleteAllRangBuoc(conn, yeuCau.getId());

			insertChiTiet(conn, yeuCau.getId(), yeuCau.getChiTiet());
			insertRangBuoc(conn, yeuCau.getId(), yeuCau.getRangBuoc());

			return yeuCau;

		}
		catch(SQLException e) {
			throw new DataAccessException("Error updating YeuCau", e);
		}
	}

	@Override
	public Optional<YeuCau> findById(Long id)
	{
		String sql = """
				SELECT yc.*,
				       nd.id AS nguoi_dung_id,
				       nd.ten_dang_nhap,
				       nd.mat_khau_hash,
				       v.id AS vai_tro_id,
				       v.ten_vai_tro,
				       dsl.id AS danh_sach_lop_id,
				       dsl.ten_danh_sach
				FROM yeu_cau yc
				JOIN nguoi_dung nd ON yc.nguoi_tao_id = nd.id
				LEFT JOIN vai_tro v ON nd.role_id = v.id
				JOIN danh_sach_lop dsl ON yc.danh_sach_lop_id = dsl.id
				WHERE yc.id = ?
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
			throw new DataAccessException("Error finding YeuCau by id", e);
		}
	}

	@Override
	public List<YeuCau> findByNguoiTaoId(Long nguoiTaoId)
	{
		return findByColumn("nguoi_tao_id", nguoiTaoId);
	}

	@Override
	public List<YeuCau> findByDanhSachLopId(Long danhSachLopId)
	{
		return findByColumn("danh_sach_lop_id", danhSachLopId);
	}

	@Override
	public void deleteById(Long id)
	{
		String sql = "DELETE FROM yeu_cau WHERE id = ?";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("YeuCau", id);

		}
		catch(SQLException e) {
			throw new DataAccessException("Error deleting YeuCau", e);
		}
	}

	private YeuCau loadAggregate(Connection conn, ResultSet rs) throws Exception
	{
		Long id = rs.getLong("id");

		// 1️⃣ Load NguoiDung
		NguoiDung nguoiDung = NguoiDungJdbcMapper.toDomain(rs);

		// 2️⃣ Load DanhSachLop context
		Long danhSachLopId = rs.getLong("danh_sach_lop_id");

		HocKy hocKy = loadHocKy(conn, danhSachLopId);
		List<DanhSachLopChiTiet> chiTietDSL = loadDanhSachLopChiTiet(conn, danhSachLopId);

		DanhSachLop danhSachLop = DanhSachLopJdbcMapper.toDomain(
				rs,
				nguoiDung,
				hocKy,
				chiTietDSL);

		YeuCau yc = YeuCau.reconstruct(
				id,
				nguoiDung,
				danhSachLop,
				rs.getString("ten_yeu_cau"),
				rs.getTimestamp("ngay_tao").toLocalDateTime(),
				Collections.emptyList(),
				Collections.emptyList());

		List<YeuCauChiTiet> chiTietList = loadChiTiet(conn, id);
		List<RangBuocToiUu> rangBuocList = loadRangBuoc(conn, yc);

		return YeuCau.reconstruct(
				id,
				nguoiDung,
				danhSachLop,
				rs.getString("ten_yeu_cau"),
				rs.getTimestamp("ngay_tao").toLocalDateTime(),
				chiTietList,
				rangBuocList);
	}

	private List<YeuCau> findByColumn(String column, Long value)
	{
		String sql = "SELECT id FROM yeu_cau WHERE " + column + " = ?";

		List<YeuCau> result = new ArrayList<>();

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, value);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					findById(rs.getLong("id")).ifPresent(result::add);
				}
			}

			return result;

		}
		catch(Exception e) {
			throw new DataAccessException("Error finding YeuCau by " + column, e);
		}
	}

	private HocKy loadHocKy(Connection conn, Long danhSachLopId) throws SQLException
	{
		String sql = """
				SELECT hk.*
				FROM danh_sach_lop dsl
				LEFT JOIN hoc_ky hk ON dsl.hoc_ky_id = hk.id
				WHERE dsl.id = ?
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, danhSachLopId);

			try (ResultSet rs = ps.executeQuery()) {
				if(!rs.next() || rs.getLong("id") == 0)
					return null;

				return HocKyJdbcMapper.toDomain(rs);
			}
		}
	}

	private List<DanhSachLopChiTiet> loadDanhSachLopChiTiet(
			Connection conn,
			Long danhSachLopId) throws Exception
	{

		String sql = """
				SELECT ct.*, lhp.*
				FROM danh_sach_lop_chi_tiet ct
				JOIN lop_hoc_phan lhp
				     ON ct.lop_hoc_phan_id = lhp.id
				WHERE ct.danh_sach_lop_id = ?
				""";

		List<DanhSachLopChiTiet> result = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, danhSachLopId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {

					LopHocPhan lopHocPhan = LopHocPhanJdbcMapper.toDomain(rs);

					result.add(
							DanhSachLopChiTietJdbcMapper.toDomain(
									rs,
									lopHocPhan));
				}
			}
		}

		return result;
	}

	private List<YeuCauChiTiet> loadChiTiet(Connection conn, Long yeuCauId) throws Exception
	{
		String sql = """
				SELECT *
				FROM yeu_cau_chi_tiet
				WHERE yeu_cau_id = ?
				""";

		List<YeuCauChiTiet> result = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, yeuCauId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					result.add(YeuCauChiTietJdbcMapper.toDomain(rs));
				}
			}
		}

		return result;
	}

	private List<RangBuocToiUu> loadRangBuoc(Connection conn, YeuCau yeuCau) throws Exception
	{

		String sql = """
				SELECT rb.*, nd.id as nguoi_tao_id, nd.ten_dang_nhap
				FROM rang_buoc_toi_uu rb
				LEFT JOIN nguoi_dung nd ON rb.nguoi_tao_id = nd.id
				WHERE rb.yeu_cau_id = ?
				""";

		List<RangBuocToiUu> result = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, yeuCau.getId());

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {

					NguoiDung nguoiTao = null;
					if(rs.getLong("nguoi_tao_id") != 0) {
						nguoiTao = NguoiDungJdbcMapper.toDomain(rs);
					}

					result.add(
							RangBuocToiUuJdbcMapper.toDomain(
									rs,
									yeuCau,
									null,
									nguoiTao));
				}
			}
		}

		return result;
	}

	private void insertChiTiet(Connection conn, Long yeuCauId,
			Collection<YeuCauChiTiet> chiTietList) throws SQLException
	{
		if(chiTietList == null || chiTietList.isEmpty())
			return;

		String sql = """
				INSERT INTO yeu_cau_chi_tiet
				(yeu_cau_id, lop_hoc_phan_id, bat_buoc, loai_chi_dinh, trong_so)
				VALUES (?, ?, ?, ?, ?)
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for(YeuCauChiTiet ct : chiTietList) {
				ps.setLong(1, yeuCauId);
				ps.setLong(2, ct.getLopHocPhan().getId());
				ps.setInt(3, ct.isBatBuoc() ? 1 : 0);
				ps.setString(4, ct.getLoaiChiDinh());
				if(ct.getTrongSo() != null)
					ps.setDouble(5, ct.getTrongSo());
				else
					ps.setNull(5, Types.DOUBLE);

				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	private void insertRangBuoc(Connection conn, Long yeuCauId,
			List<RangBuocToiUu> list) throws SQLException
	{
		if(list == null || list.isEmpty())
			return;

		String sql = """
				INSERT INTO rang_buoc_toi_uu
				(yeu_cau_id, loai_rang_buoc, target_type, target_value,
				 attribute, operator, value, la_cung, trong_so, ghi_chu, nguoi_tao_id)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for(RangBuocToiUu rb : list) {
				ps.setLong(1, yeuCauId);
				ps.setString(2, rb.getLoaiRangBuoc());
				ps.setString(3, rb.getTargetType());
				ps.setString(4, rb.getTargetValue());
				ps.setString(5, rb.getAttribute());
				ps.setString(6, rb.getOperator());
				ps.setString(7, rb.getValue());
				ps.setInt(8, rb.isLaCung() ? 1 : 0);
				ps.setDouble(9, rb.getTrongSo());
				ps.setString(10, rb.getGhiChu());

				if(rb.getNguoiTao() != null)
					ps.setLong(11, rb.getNguoiTao().getId());
				else
					ps.setNull(11, Types.BIGINT);

				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	private void deleteAllChiTiet(Connection conn, Long yeuCauId)
			throws SQLException
	{
		try (PreparedStatement ps = conn.prepareStatement("DELETE FROM yeu_cau_chi_tiet WHERE yeu_cau_id = ?")) {
			ps.setLong(1, yeuCauId);
			ps.executeUpdate();
		}
	}

	private void deleteAllRangBuoc(Connection conn, Long yeuCauId)
			throws SQLException
	{
		try (PreparedStatement ps = conn.prepareStatement("DELETE FROM rang_buoc_toi_uu WHERE yeu_cau_id = ?")) {
			ps.setLong(1, yeuCauId);
			ps.executeUpdate();
		}
	}
}