package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.TepTaiLenRepositoryPort;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.io.File;
import java.sql.*;

public class JdbcTepTaiLenRepository implements TepTaiLenRepositoryPort
{
	@Override
	public Long saveMetadata(Long nguoiTaoId, File file, String loaiTep) throws SQLException
	{
		String sql = "INSERT INTO tep_tai_len (nguoi_tao_id, ten_tep_goc, loai_tep, duong_dan, storage_type, kich_thuoc) VALUES (?,?,?,?,?,?)";
		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, nguoiTaoId);
			ps.setString(2, file.getName());
			ps.setString(3, loaiTep);
			ps.setString(4, file.getAbsolutePath());
			ps.setString(5, "PATH");
			ps.setLong(6, file.length());
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) return rs.getLong(1);
			}
		}
		throw new SQLException("Cannot create tep_tai_len");
	}
}
