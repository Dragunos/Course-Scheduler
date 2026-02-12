package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.dto.DanhSachLopChiTietDto;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.LichHocDto;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepositoryPort;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.time.ZoneId;
import java.util.*;

public class JdbcDanhSachLopRepository implements DanhSachLopRepositoryPort
{
	@Override
	public int save(String tenDanhSach, int nguoiTaoId, boolean laCongKhai, Integer hocKyId)
			throws SQLException
	{
		String sql = "INSERT INTO danh_sach_lop (ten_danh_sach, nguoi_tao_id, la_cong_khai, hoc_ky_id) VALUES (?,?,?,?)";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, tenDanhSach);
			ps.setInt(2, nguoiTaoId);
			ps.setInt(3, laCongKhai ? 1 : 0);
			if(hocKyId != null) ps.setInt(4, hocKyId);
			else ps.setNull(4, Types.INTEGER);
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) return rs.getInt(1);
			}
		}
		throw new SQLException("Cannot create danh_sach_lop");
	}

	@Override
	public void addChiTiet(int danhSachId, int lopHocPhanId) throws SQLException
	{
		String insert = "INSERT OR IGNORE INTO danh_sach_lop_chi_tiet (danh_sach_lop_id, lop_hoc_phan_id, bat_buoc) VALUES (?,?,0)";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(insert)) {
			ps.setInt(1, danhSachId);
			ps.setInt(2, lopHocPhanId);
			ps.executeUpdate();
		}
	}

	@Override
	public List<DanhSachLopDto> findByNguoiTaoOrShared(int nguoiDungId) throws SQLException
	{
		String sql = "SELECT d.id, d.ten_danh_sach, d.nguoi_tao_id, d.la_cong_khai, d.hoc_ky_id, d.ngay_tao " +
				"FROM danh_sach_lop d " +
				"LEFT JOIN chia_se_danh_sach_lop cs ON cs.danh_sach_lop_id = d.id " +
				"WHERE d.nguoi_tao_id = ? OR cs.nguoi_dung_id = ? " +
				"GROUP BY d.id ORDER BY d.ngay_tao DESC";

		List<DanhSachLopDto> list = new ArrayList<>();

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, nguoiDungId);
			ps.setInt(2, nguoiDungId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					DanhSachLopDto dto = new DanhSachLopDto();
					dto.id = rs.getLong("id");
					dto.tenDanhSach = rs.getString("ten_danh_sach");
					dto.nguoiTaoId = rs.getLong("nguoi_tao_id");
					dto.laCongKhai = rs.getInt("la_cong_khai");
					dto.hocKyId = rs.getObject("hoc_ky_id") != null ? rs.getLong("hoc_ky_id") : null;

					Timestamp ts = rs.getTimestamp("ngay_tao");
					dto.ngayTao = ts != null
							? ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
							: null;

					list.add(dto);
				}
			}
		}
		return list;
	}

	@Override
	public Optional<DanhSachLopDto> findByIdWithDetails(int danhSachId) throws SQLException
	{
		String headerSql = "SELECT * FROM danh_sach_lop WHERE id=?";
		String detailSql = "SELECT lhp.id AS lhp_id, lhp.ma_lop, hp.ten_hoc_phan, hp.so_tin_chi, " +
				"gv.ten_giang_vien, lhp.hinh_thuc_day, lhp.dia_diem, " +
				"lh.thu, lh.tiet_bat_dau, lh.tiet_ket_thuc " +
				"FROM danh_sach_lop_chi_tiet dct " +
				"JOIN lop_hoc_phan lhp ON lhp.id=dct.lop_hoc_phan_id " +
				"LEFT JOIN hoc_phan hp ON hp.id=lhp.hoc_phan_id " +
				"LEFT JOIN giang_vien gv ON gv.id=lhp.giang_vien_id " +
				"LEFT JOIN lich_hoc lh ON lh.lop_hoc_phan_id=lhp.id " +
				"WHERE dct.danh_sach_lop_id=? " +
				"ORDER BY lhp.id";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ph = conn.prepareStatement(headerSql)) {
			ph.setInt(1, danhSachId);

			try (ResultSet rh = ph.executeQuery()) {
				if(!rh.next()) return Optional.empty();

				DanhSachLopDto dto = new DanhSachLopDto();
				dto.id = rh.getLong("id");
				dto.tenDanhSach = rh.getString("ten_danh_sach");
				dto.nguoiTaoId = rh.getLong("nguoi_tao_id");
				dto.laCongKhai = rh.getInt("la_cong_khai");
				dto.hocKyId = rh.getObject("hoc_ky_id") != null ? rh.getLong("hoc_ky_id") : null;

				dto.chiTiet = new ArrayList<>();

				Map<Long, DanhSachLopChiTietDto> map = new LinkedHashMap<>();

				try (PreparedStatement pd = conn.prepareStatement(detailSql)) {
					pd.setInt(1, danhSachId);

					try (ResultSet rd = pd.executeQuery()) {
						while(rd.next()) {
							long lhpId = rd.getLong("lhp_id");

							DanhSachLopChiTietDto item = map.get(lhpId);
							if(item == null) {
								item = new DanhSachLopChiTietDto();
								item.lopHocPhanId = lhpId;
								item.maLop = rd.getString("ma_lop");
								item.tenHocPhan = rd.getString("ten_hoc_phan");
								item.soTinChi = rd.getObject("so_tin_chi") != null ? rd.getInt("so_tin_chi") : null;
								item.tenGiangVien = rd.getString("ten_giang_vien");
								item.hinhThucDay = rd.getString("hinh_thuc_day");
								item.diaDiem = rd.getString("dia_diem");
								item.lichHoc = new ArrayList<>();
								map.put(lhpId, item);
							}

							if(rd.getObject("thu") != null) {
								LichHocDto lh = new LichHocDto();
								lh.thu = rd.getInt("thu");
								lh.tietBatDau = rd.getInt("tiet_bat_dau");
								lh.tietKetThuc = rd.getInt("tiet_ket_thuc");
								item.lichHoc.add(lh);
							}
						}
					}
				}

				dto.chiTiet.addAll(map.values());
				return Optional.of(dto);
			}
		}
	}

	@Override
	public boolean isCreator(int danhSachId, int nguoiDungId) throws SQLException
	{
		String sql = "SELECT 1 FROM danh_sach_lop WHERE id=? AND nguoi_tao_id=? LIMIT 1";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, danhSachId);
			ps.setInt(2, nguoiDungId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	@Override
	public boolean isShared(int danhSachId, int nguoiDungId) throws SQLException
	{
		String sql = "SELECT 1 FROM chia_se_danh_sach_lop WHERE danh_sach_lop_id=? AND nguoi_dung_id=? LIMIT 1";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, danhSachId);
			ps.setInt(2, nguoiDungId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	@Override
	public void updateHeader(int danhSachId, String tenDanhSach, Integer hocKyId) throws SQLException
	{
		String sql = "UPDATE danh_sach_lop SET ten_danh_sach=?, hoc_ky_id=? WHERE id=?";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, tenDanhSach);
			if(hocKyId != null) ps.setInt(2, hocKyId);
			else ps.setNull(2, Types.INTEGER);
			ps.setInt(3, danhSachId);
			ps.executeUpdate();
		}
	}

	@Override
	public void deleteAllChiTiet(int danhSachId) throws SQLException
	{
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"DELETE FROM danh_sach_lop_chi_tiet WHERE danh_sach_lop_id=?")) {
			ps.setInt(1, danhSachId);
			ps.executeUpdate();
		}
	}

	@Override
	public void deleteDanhSach(int danhSachId) throws SQLException
	{
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"DELETE FROM danh_sach_lop WHERE id=?")) {
			ps.setInt(1, danhSachId);
			ps.executeUpdate();
		}
	}
}