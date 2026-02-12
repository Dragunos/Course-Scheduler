package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.LopHocPhanRepositoryPort;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.util.Optional;

public class JdbcLopHocPhanRepository implements LopHocPhanRepositoryPort
{
	@Override
	public Optional<Integer> findIdByMaAndHocPhanId(String maLop, int hocPhanId) throws SQLException
	{
		String sql = "SELECT id FROM lop_hoc_phan WHERE ma_lop = ? AND hoc_phan_id = ?";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, maLop);
			ps.setInt(2, hocPhanId);
			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) return Optional.of(rs.getInt(1));
			}
		}
		return Optional.empty();
	}

	@Override
	public int save(LopHocPhan lop) throws SQLException
	{
		String insert = "INSERT INTO lop_hoc_phan (ma_lop, hoc_phan_id, giang_vien_id, hinh_thuc_day, dia_diem) VALUES (?,?,?,?,?)";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, lop.getMaLop());
			ps.setInt(2, lop.getHocPhanId());
			if(lop.getGiangVienId() != null) ps.setInt(3, lop.getGiangVienId());
			else ps.setNull(3, Types.INTEGER);
			ps.setString(4, lop.getHinhThucDay());
			ps.setString(5, lop.getDiaDiem());
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) return rs.getInt(1);
			}
		}
		throw new SQLException("Cannot create lop_hoc_phan");
	}
}