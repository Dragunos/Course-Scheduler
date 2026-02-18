package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.exception.*;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepository;
import vn.edu.haui.scheduler.domain.enums.HinhThucDay;
import vn.edu.haui.scheduler.domain.enums.ThuTrongTuan;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class JdbcDanhSachLopRepository implements DanhSachLopRepository
{
	@Override
	public Long save(String tenDanhSach, Long nguoiTaoId, boolean laCongKhai, Long hocKyId)
	{
		String sql = """
				INSERT INTO danh_sach_lop
				(ten_danh_sach, nguoi_tao_id, la_cong_khai, hoc_ky_id)
				VALUES (?, ?, ?, ?)
				""";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, tenDanhSach);
			ps.setLong(2, nguoiTaoId);
			ps.setInt(3, laCongKhai ? 1 : 0);

			if(hocKyId != null)
				ps.setLong(4, hocKyId);
			else
				ps.setNull(4, Types.BIGINT);

			int affected = ps.executeUpdate();
			if(affected == 0)
				throw new DataAccessException("Insert danh_sach_lop failed");

			try (ResultSet keys = ps.getGeneratedKeys()) {
				if(keys.next()) return keys.getLong(1);
			}

			throw new DataAccessException("No ID returned");

		}
		catch(SQLException ex) {
			throw new DataAccessException("Failed to save DanhSachLop", ex);
		}
	}

	@Override
	public void addChiTiet(Long danhSachId, Long lopHocPhanId)
	{
		String sql = """
				INSERT OR IGNORE INTO danh_sach_lop_chi_tiet
				(danh_sach_lop_id, lop_hoc_phan_id, bat_buoc)
				VALUES (?, ?, 0)
				""";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, danhSachId);
			ps.setLong(2, lopHocPhanId);
			ps.executeUpdate();

		}
		catch(SQLException ex) {
			throw new DataAccessException("Failed to addChiTiet", ex);
		}
	}

	@Override
	public List<DanhSachLop> findByNguoiTaoOrShared(Long nguoiDungId)
	{
		String sql = """
				SELECT d.id, d.ten_danh_sach, d.nguoi_tao_id,
				       d.la_cong_khai, d.hoc_ky_id, d.ngay_tao
				FROM danh_sach_lop d
				LEFT JOIN chia_se_danh_sach_lop cs
				       ON cs.danh_sach_lop_id = d.id
				WHERE d.nguoi_tao_id = ?
				   OR cs.nguoi_dung_id = ?
				GROUP BY d.id
				ORDER BY d.ngay_tao DESC
				""";

		List<DanhSachLop> result = new ArrayList<>();

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, nguoiDungId);
			ps.setLong(2, nguoiDungId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					result.add(mapHeader(rs));
				}
			}

			return result;

		}
		catch(SQLException ex) {
			throw new DataAccessException("Failed to findByNguoiTaoOrShared", ex);
		}
	}

	@Override
	public Optional<DanhSachLop> findByIdWithDetails(Long danhSachId)
	{
		String sql = """
				SELECT
				    dsl.id,
				    dsl.ten_danh_sach,
				    dsl.nguoi_tao_id,
				    dsl.la_cong_khai,
				    dsl.hoc_ky_id,
				    dsl.ngay_tao,

				    ct.lop_hoc_phan_id,
				    ct.bat_buoc,

				    lhp.ma_lop,
				    lhp.hinh_thuc_day,
				    lhp.dia_diem,

				    hp.id AS hoc_phan_id,
				    hp.ten_hoc_phan,
				    hp.so_tin_chi,

				    gv.id AS giang_vien_id,
				    gv.ten_giang_vien,

				    lh.id AS lich_id,
				    lh.thu,
				    lh.tiet_bat_dau,
				    lh.tiet_ket_thuc

				FROM danh_sach_lop dsl
				LEFT JOIN danh_sach_lop_chi_tiet ct
				    ON dsl.id = ct.danh_sach_lop_id
				LEFT JOIN lop_hoc_phan lhp
				    ON ct.lop_hoc_phan_id = lhp.id
				LEFT JOIN hoc_phan hp
				    ON lhp.hoc_phan_id = hp.id
				LEFT JOIN giang_vien gv
				    ON lhp.giang_vien_id = gv.id
				LEFT JOIN lich_hoc lh
				    ON lhp.id = lh.lop_hoc_phan_id
				WHERE dsl.id = ?
				""";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, danhSachId);

			try (ResultSet rs = ps.executeQuery()) {

				if(!rs.next()) return Optional.empty();

				DanhSachLop danhSach = mapHeader(rs);
				Map<Long, DanhSachLopChiTiet> chiTietMap = new LinkedHashMap<>();

				do {
					Long lopHocPhanId = rs.getObject("lop_hoc_phan_id", Long.class);
					if(lopHocPhanId == null) continue;

					DanhSachLopChiTiet chiTiet = chiTietMap.get(lopHocPhanId);

					if(chiTiet == null) {

						HocPhan hocPhan = new HocPhan(
								rs.getLong("hoc_phan_id"),
								null,
								rs.getString("ten_hoc_phan"),
								rs.getInt("so_tin_chi"));

						GiangVien giangVien = new GiangVien(
								rs.getLong("giang_vien_id"),
								rs.getString("ten_giang_vien"));

						String htd = rs.getString("hinh_thuc_day");
						HinhThucDay hinhThuc = htd != null ? HinhThucDay.valueOf(htd)
								: HinhThucDay.KHONG_XAC_DINH;

						LopHocPhan lop = new LopHocPhan(
								lopHocPhanId,
								rs.getString("ma_lop"),
								hocPhan,
								giangVien,
								hinhThuc,
								rs.getString("dia_diem"),
								new ArrayList<>());

						chiTiet = new DanhSachLopChiTiet(
								lop,
								rs.getInt("bat_buoc") == 1);

						chiTietMap.put(lopHocPhanId, chiTiet);
					}

					Long lichId = rs.getObject("lich_id", Long.class);
					if(lichId != null) {
						LichHoc lich = new LichHoc(
								lichId,
								lopHocPhanId,
								ThuTrongTuan.fromGiaTri(rs.getInt("thu")),
								rs.getInt("tiet_bat_dau"),
								rs.getInt("tiet_ket_thuc"));

						chiTiet.getLopHocPhan().themLichHoc(lich);
					}

				} while(rs.next());

				return Optional.of(
						new DanhSachLop(
								danhSach.getId(),
								danhSach.getTenDanhSach(),
								danhSach.getNguoiTao(),
								danhSach.isCongKhai(),
								danhSach.getHocKy(),
								danhSach.getNgayTao(),
								new ArrayList<>(chiTietMap.values())));

			}

		}
		catch(SQLException ex) {
			throw new DataAccessException("Failed to findByIdWithDetails", ex);
		}
	}

	@Override
	public boolean isCreator(Long danhSachId, Long nguoiDungId)
	{
		String sql = "SELECT 1 FROM danh_sach_lop WHERE id = ? AND nguoi_tao_id = ? LIMIT 1";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, danhSachId);
			ps.setLong(2, nguoiDungId);

			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}

		}
		catch(SQLException ex) {
			throw new DataAccessException("Failed to check isCreator", ex);
		}
	}

	@Override
	public boolean isShared(Long danhSachId, Long nguoiDungId)
	{
		String sql = "SELECT 1 FROM chia_se_danh_sach_lop WHERE danh_sach_lop_id = ? AND nguoi_dung_id = ? LIMIT 1";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, danhSachId);
			ps.setLong(2, nguoiDungId);

			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}

		}
		catch(SQLException ex) {
			throw new DataAccessException("Failed to check isShared", ex);
		}
	}

	@Override
	public void updateHeader(Long danhSachId, String tenDanhSach, Long hocKyId)
	{
		String sql = "UPDATE danh_sach_lop SET ten_danh_sach = ?, hoc_ky_id = ? WHERE id = ?";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, tenDanhSach);

			if(hocKyId != null)
				ps.setLong(2, hocKyId);
			else
				ps.setNull(2, Types.BIGINT);

			ps.setLong(3, danhSachId);
			ps.executeUpdate();

		}
		catch(SQLException ex) {
			throw new DataAccessException("Failed to updateHeader", ex);
		}
	}

	@Override
	public void deleteAllChiTiet(Long danhSachId)
	{
		String sql = "DELETE FROM danh_sach_lop_chi_tiet WHERE danh_sach_lop_id = ?";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, danhSachId);
			ps.executeUpdate();

		}
		catch(SQLException ex) {
			throw new DataAccessException("Failed to deleteAllChiTiet", ex);
		}
	}

	@Override
	public void deleteDanhSach(Long danhSachId)
	{
		String sql = "DELETE FROM danh_sach_lop WHERE id = ?";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, danhSachId);
			ps.executeUpdate();

		}
		catch(SQLException ex) {
			throw new DataAccessException("Failed to deleteDanhSach", ex);
		}
	}

	@Override
	public List<DanhSachLop> findAllPublic()
	{
		String sql = """
				SELECT id, ten_danh_sach, nguoi_tao_id,
				       la_cong_khai, hoc_ky_id, ngay_tao
				FROM danh_sach_lop
				WHERE la_cong_khai = 1
				ORDER BY ngay_tao DESC
				""";

		List<DanhSachLop> result = new ArrayList<>();

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while(rs.next()) {
				result.add(mapHeader(rs));
			}

			return result;

		}
		catch(SQLException ex) {
			throw new DataAccessException("Failed to findAllPublic", ex);
		}
	}

	@Override
	public boolean isPublic(Long danhSachId)
	{
		String sql = "SELECT la_cong_khai FROM danh_sach_lop WHERE id = ?";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, danhSachId);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next())
					return rs.getInt("la_cong_khai") == 1;
			}

			return false;

		}
		catch(SQLException ex) {
			throw new DataAccessException("Failed to check isPublic", ex);
		}
	}

	private DanhSachLop mapHeader(ResultSet rs) throws SQLException
	{
		Timestamp ts = rs.getTimestamp("ngay_tao");
		LocalDateTime ngayTao = ts != null ? ts.toLocalDateTime() : null;

		Long nguoiTaoId = rs.getLong("nguoi_tao_id");
		Long hocKyId = rs.getObject("hoc_ky_id") != null
				? rs.getLong("hoc_ky_id")
				: null;

		NguoiDung nguoiTao = new NguoiDung(
				nguoiTaoId,
				null,
				null,
				null,
				null);

		HocKy hocKy = hocKyId != null
				? new HocKy(hocKyId, "UNKNOWN", "UNKNOWN")
				: null;

		return new DanhSachLop(
				rs.getLong("id"),
				rs.getString("ten_danh_sach"),
				nguoiTao,
				rs.getInt("la_cong_khai") == 1,
				hocKy,
				ngayTao,
				new ArrayList<>());
	}
}