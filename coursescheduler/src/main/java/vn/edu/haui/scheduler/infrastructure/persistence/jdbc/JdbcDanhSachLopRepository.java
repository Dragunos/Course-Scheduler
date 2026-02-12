package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.dto.DanhSachLopChiTietDto;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.LichHocDto;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepositoryPort;
import vn.edu.haui.scheduler.domain.enums.ThuTrongTuan;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class JdbcDanhSachLopRepository implements DanhSachLopRepositoryPort
{
	@Override
	public Long save(String tenDanhSach, Long nguoiTaoId, boolean laCongKhai, Long hocKyId)
			throws SQLException
	{
		String sql = "INSERT INTO danh_sach_lop (ten_danh_sach, nguoi_tao_id, la_cong_khai, hoc_ky_id) VALUES (?,?,?,?)";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, tenDanhSach);
			ps.setLong(2, nguoiTaoId);
			ps.setInt(3, laCongKhai ? 1 : 0);
			if(hocKyId != null) ps.setLong(4, hocKyId);
			else ps.setNull(4, Types.BIGINT);
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) return rs.getLong(1);
			}
		}
		throw new SQLException("Cannot create danh_sach_lop");
	}

	@Override
	public void addChiTiet(Long danhSachId, Long lopHocPhanId) throws SQLException
	{
		String insert = "INSERT OR IGNORE INTO danh_sach_lop_chi_tiet (danh_sach_lop_id, lop_hoc_phan_id, bat_buoc) VALUES (?,?,0)";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(insert)) {
			ps.setLong(1, danhSachId);
			ps.setLong(2, lopHocPhanId);
			ps.executeUpdate();
		}
	}

	@Override
	public List<DanhSachLopDto> findByNguoiTaoOrShared(Long nguoiDungId) throws SQLException
	{
		String sql = "SELECT d.id, d.ten_danh_sach, d.nguoi_tao_id, d.la_cong_khai, d.hoc_ky_id, d.ngay_tao " +
				"FROM danh_sach_lop d " +
				"LEFT JOIN chia_se_danh_sach_lop cs ON cs.danh_sach_lop_id = d.id " +
				"WHERE d.nguoi_tao_id = ? OR cs.nguoi_dung_id = ? " +
				"GROUP BY d.id ORDER BY d.ngay_tao DESC";

		List<DanhSachLopDto> list = new ArrayList<>();

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, nguoiDungId);
			ps.setLong(2, nguoiDungId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					Long id = rs.getLong("id");
					String ten = rs.getString("ten_danh_sach");
					Long nguoiTaoId = rs.getLong("nguoi_tao_id");
					Integer laCongKhai = rs.getInt("la_cong_khai");
					Long hocKyId = rs.getObject("hoc_ky_id") != null ? rs.getLong("hoc_ky_id") : null;

					Timestamp ts = rs.getTimestamp("ngay_tao");
					LocalDateTime ngayTao = ts != null
							? ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
							: null;

					DanhSachLopDto dto = new DanhSachLopDto(id, ten, nguoiTaoId, laCongKhai, hocKyId, ngayTao,
							Collections.emptyList());
					list.add(dto);
				}
			}
		}
		return list;
	}

	@Override
	public Optional<DanhSachLopDto> findByIdWithDetails(Long danhSachId) throws SQLException
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
			ph.setLong(1, danhSachId);

			try (ResultSet rh = ph.executeQuery()) {
				if(!rh.next()) return Optional.empty();

				Long id = rh.getLong("id");
				String ten = rh.getString("ten_danh_sach");
				Long nguoiTaoId = rh.getLong("nguoi_tao_id");
				Integer laCongKhai = rh.getInt("la_cong_khai");
				Long hocKyId = rh.getObject("hoc_ky_id") != null ? rh.getLong("hoc_ky_id") : null;
				Timestamp ts = rh.getTimestamp("ngay_tao");
				LocalDateTime ngayTao = ts != null
						? ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
						: null;

				Map<Long, DanhSachLopChiTietDto> map = new LinkedHashMap<>();

				try (PreparedStatement pd = conn.prepareStatement(detailSql)) {
					pd.setLong(1, danhSachId);

					try (ResultSet rd = pd.executeQuery()) {
						while(rd.next()) {
							Long lhpId = rd.getLong("lhp_id");

							DanhSachLopChiTietDto item = map.get(lhpId);
							if(item == null) {
								String maLop = rd.getString("ma_lop");
								String tenHocPhan = rd.getString("ten_hoc_phan");
								Integer soTinChi = rd.getObject("so_tin_chi") != null ? rd.getInt("so_tin_chi") : null;
								String tenGiangVien = rd.getString("ten_giang_vien");
								String hinhThucDay = rd.getString("hinh_thuc_day");
								String diaDiem = rd.getString("dia_diem");
								item = new DanhSachLopChiTietDto(lhpId, maLop, tenHocPhan, soTinChi, tenGiangVien,
										hinhThucDay, diaDiem, new ArrayList<>());
								map.put(lhpId, item);
							}

							if(rd.getObject("thu") != null) {
								int thuValue = rd.getInt("thu");
								ThuTrongTuan thu = ThuTrongTuan.fromGiaTri(thuValue);

								LichHocDto lh = new LichHocDto(
										thu,
										rd.getInt("tiet_bat_dau"),
										rd.getInt("tiet_ket_thuc"));

								item.getLichHoc().add(lh);
							}
						}
					}
				}

				List<DanhSachLopChiTietDto> chiTiet = new ArrayList<>(map.values());
				DanhSachLopDto dto = new DanhSachLopDto(id, ten, nguoiTaoId, laCongKhai, hocKyId, ngayTao, chiTiet);
				return Optional.of(dto);
			}
		}
	}

	@Override
	public boolean isCreator(Long danhSachId, Long nguoiDungId) throws SQLException
	{
		String sql = "SELECT 1 FROM danh_sach_lop WHERE id=? AND nguoi_tao_id=? LIMIT 1";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, danhSachId);
			ps.setLong(2, nguoiDungId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	@Override
	public boolean isShared(Long danhSachId, Long nguoiDungId) throws SQLException
	{
		String sql = "SELECT 1 FROM chia_se_danh_sach_lop WHERE danh_sach_lop_id=? AND nguoi_dung_id=? LIMIT 1";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, danhSachId);
			ps.setLong(2, nguoiDungId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	@Override
	public void updateHeader(Long danhSachId, String tenDanhSach, Long hocKyId) throws SQLException
	{
		String sql = "UPDATE danh_sach_lop SET ten_danh_sach=?, hoc_ky_id=? WHERE id=?";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, tenDanhSach);

			if(hocKyId != null) ps.setLong(2, hocKyId);
			else ps.setNull(2, Types.BIGINT);

			ps.setLong(3, danhSachId);
			ps.executeUpdate();
		}
	}

	@Override
	public void deleteAllChiTiet(Long danhSachId) throws SQLException
	{
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"DELETE FROM danh_sach_lop_chi_tiet WHERE danh_sach_lop_id=?")) {
			ps.setLong(1, danhSachId);
			ps.executeUpdate();
		}
	}

	@Override
	public void deleteDanhSach(Long danhSachId) throws SQLException
	{
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"DELETE FROM danh_sach_lop WHERE id=?")) {
			ps.setLong(1, danhSachId);
			ps.executeUpdate();
		}
	}
}
