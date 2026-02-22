package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.out.ThoiKhoaBieuRepository;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManagerImpl;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.*;

import java.sql.*;
import java.util.*;

public class JdbcThoiKhoaBieuRepository implements ThoiKhoaBieuRepository
{
	private final TransactionManagerImpl transactionManager;

	public JdbcThoiKhoaBieuRepository(TransactionManagerImpl transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	@Override
	public ThoiKhoaBieu save(ThoiKhoaBieu tkb)
	{
		if(tkb == null)
			throw new ValidationException("ThoiKhoaBieu must not be null");

		if(!tkb.isPersisted())
			return insert(tkb);

		return update(tkb);
	}

	private ThoiKhoaBieu insert(ThoiKhoaBieu tkb)
	{
		String sql = """
				INSERT INTO thoi_khoa_bieu
				(nguoi_dung_id, danh_sach_lop_id, ten_phuong_an, diem_danh_gia, ngay_tao)
				VALUES (?, ?, ?, ?, ?)
				""";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, tkb.getNguoiDung().getId());
			ps.setLong(2, tkb.getDanhSachLop().getId());
			ps.setString(3, tkb.getTenPhuongAn());

			if(tkb.getDiemDanhGia() != null)
				ps.setDouble(4, tkb.getDiemDanhGia());
			else
				ps.setNull(4, Types.REAL);

			ps.setTimestamp(5, Timestamp.valueOf(tkb.getNgayTao()));

			ps.executeUpdate();

			Long generatedId;

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(!rs.next())
					throw new DataAccessException("Failed to retrieve ThoiKhoaBieu id", null);

				generatedId = rs.getLong(1);
			}

			insertChiTiet(conn, generatedId, tkb.getCacLop());

			return ThoiKhoaBieu.reconstruct(
					generatedId,
					tkb.getNguoiDung(),
					tkb.getDanhSachLop(),
					tkb.getTenPhuongAn(),
					tkb.getDiemDanhGia(),
					tkb.getNgayTao(),
					tkb.getCacLop());
		}
		catch(SQLException e) {
			throw new DataAccessException("Error inserting ThoiKhoaBieu", e);
		}
	}

	private ThoiKhoaBieu update(ThoiKhoaBieu tkb)
	{
		String sql = """
				UPDATE thoi_khoa_bieu
				SET ten_phuong_an = ?, diem_danh_gia = ?
				WHERE id = ?
				""";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, tkb.getTenPhuongAn());

			if(tkb.getDiemDanhGia() != null)
				ps.setDouble(2, tkb.getDiemDanhGia());
			else
				ps.setNull(2, Types.REAL);

			ps.setLong(3, tkb.getId());

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("ThoiKhoaBieu", tkb.getId());

			deleteAllChiTiet(conn, tkb.getId());
			insertChiTiet(conn, tkb.getId(), tkb.getCacLop());

			return tkb;
		}
		catch(SQLException e) {
			throw new DataAccessException("Error updating ThoiKhoaBieu", e);
		}
	}

	@Override
	public Optional<ThoiKhoaBieu> findById(Long id)
	{
		String sql = """
				SELECT tkb.*,
				       nd.id as nd_id, nd.ten_dang_nhap, nd.mat_khau_hash,
				       nd.ngay_tao as nd_ngay_tao,
				       dsl.id as dsl_id
				FROM thoi_khoa_bieu tkb
				JOIN nguoi_dung nd ON tkb.nguoi_dung_id = nd.id
				JOIN danh_sach_lop dsl ON tkb.danh_sach_lop_id = dsl.id
				WHERE tkb.id = ?
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
			throw new DataAccessException("Error finding ThoiKhoaBieu by id", e);
		}
	}

	@Override
	public List<ThoiKhoaBieu> findByNguoiDungId(Long nguoiDungId)
	{
		return findByQuery(
				"SELECT id FROM thoi_khoa_bieu WHERE nguoi_dung_id = ?",
				nguoiDungId);
	}

	@Override
	public List<ThoiKhoaBieu> findByDanhSachLopId(Long danhSachLopId)
	{
		return findByQuery(
				"SELECT id FROM thoi_khoa_bieu WHERE danh_sach_lop_id = ?",
				danhSachLopId);
	}

	private List<ThoiKhoaBieu> findByQuery(String sql, Long param)
	{
		List<ThoiKhoaBieu> result = new ArrayList<>();

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, param);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					findById(rs.getLong("id")).ifPresent(result::add);
				}
			}

			return result;
		}
		catch(Exception e) {
			throw new DataAccessException("Error querying ThoiKhoaBieu", e);
		}
	}

	@Override
	public void deleteById(Long id)
	{
		String sql = "DELETE FROM thoi_khoa_bieu WHERE id = ?";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("ThoiKhoaBieu", id);
		}
		catch(SQLException e) {
			throw new DataAccessException("Error deleting ThoiKhoaBieu", e);
		}
	}

	private ThoiKhoaBieu loadAggregate(Connection conn, ResultSet rs) throws Exception
	{
		Long id = rs.getLong("id");

		NguoiDung nguoiDung = NguoiDungJdbcMapper.toDomain(rs);

		DanhSachLop danhSachLop = loadDanhSachLop(conn, rs.getLong("danh_sach_lop_id"));

		Set<LopHocPhan> cacLop = loadChiTiet(conn, id);

		return ThoiKhoaBieuJdbcMapper.toDomain(
				rs,
				nguoiDung,
				danhSachLop,
				cacLop);
	}

	private Set<LopHocPhan> loadChiTiet(Connection conn, Long tkbId) throws Exception
	{
		String sql = """
				SELECT lhp.*
				FROM thoi_khoa_bieu_chi_tiet ct
				JOIN lop_hoc_phan lhp ON ct.lop_hoc_phan_id = lhp.id
				WHERE ct.thoi_khoa_bieu_id = ?
				""";

		Set<LopHocPhan> result = new HashSet<>();

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, tkbId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					result.add(LopHocPhanJdbcMapper.toDomain(rs));
				}
			}
		}

		return result;
	}

	private void insertChiTiet(Connection conn, Long tkbId, Set<LopHocPhan> cacLop)
			throws SQLException
	{
		if(cacLop == null || cacLop.isEmpty())
			return;

		String sql = """
				INSERT INTO thoi_khoa_bieu_chi_tiet
				(thoi_khoa_bieu_id, lop_hoc_phan_id)
				VALUES (?, ?)
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for(LopHocPhan lop : cacLop) {
				ps.setLong(1, tkbId);
				ps.setLong(2, lop.getId());
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	private void deleteAllChiTiet(Connection conn, Long tkbId)
			throws SQLException
	{
		try (PreparedStatement ps = conn.prepareStatement(
				"DELETE FROM thoi_khoa_bieu_chi_tiet WHERE thoi_khoa_bieu_id = ?")) {
			ps.setLong(1, tkbId);
			ps.executeUpdate();
		}
	}

	private DanhSachLop loadDanhSachLop(Connection conn, Long id) throws Exception
	{
		String sql = "SELECT * FROM danh_sach_lop WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(!rs.next())
					throw new IllegalStateException("DanhSachLop not found");

				NguoiDung nguoiTao = null;
				HocKy hocKy = null;

				return DanhSachLopJdbcMapper.toDomain(
						rs,
						nguoiTao,
						hocKy,
						Collections.emptyList(),
						Collections.emptySet());
			}
		}
	}
}