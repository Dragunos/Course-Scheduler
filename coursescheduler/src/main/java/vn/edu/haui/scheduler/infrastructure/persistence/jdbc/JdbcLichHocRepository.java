package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.LichHocRepositoryPort;
import vn.edu.haui.scheduler.infrastructure.io.imports.ImportedLopRow;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JdbcLichHocRepository implements LichHocRepositoryPort
{
	@Override
	public void saveAll(Long lopHocPhanId, List<ImportedLopRow.Buoi> buois) throws SQLException
	{
		if(buois == null || buois.isEmpty()) return;

		String insert = "INSERT INTO lich_hoc (lop_hoc_phan_id, thu, tiet_bat_dau, tiet_ket_thuc) VALUES (?,?,?,?)";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(insert)) {

			for(ImportedLopRow.Buoi b : buois) {
				ps.setLong(1, lopHocPhanId);
				ps.setInt(2, b.thu);
				ps.setInt(3, b.tietBatDau);
				ps.setInt(4, b.tietKetThuc);
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}
}
