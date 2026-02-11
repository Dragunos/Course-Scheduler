package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepositoryPort;

import java.sql.*;

public class JdbcDanhSachLopRepository implements DanhSachLopRepositoryPort
{
	@Override
	public int save(Connection conn, String tenDanhSach, int nguoiTaoId, boolean laCongKhai, Integer hocKyId)
			throws SQLException
	{
		String sql = "INSERT INTO danh_sach_lop (ten_danh_sach, nguoi_tao_id, la_cong_khai, hoc_ky_id) VALUES (?,?,?,?)";
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
	public void addChiTiet(Connection conn, int danhSachId, int lopHocPhanId) throws SQLException
	{
		String insert = "INSERT OR IGNORE INTO danh_sach_lop_chi_tiet (danh_sach_lop_id, lop_hoc_phan_id, bat_buoc) VALUES (?,?,0)";
		try (PreparedStatement ps = conn.prepareStatement(insert)) {
			ps.setInt(1, danhSachId);
			ps.setInt(2, lopHocPhanId);
			ps.executeUpdate();
		}
	}
}