package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.YeuCauChiTietRepository;
import vn.edu.haui.scheduler.domain.enums.LoaiChiDinh;
import vn.edu.haui.scheduler.domain.model.YeuCauChiTiet;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcYeuCauChiTietRepository implements YeuCauChiTietRepository
{
	@Override
	public void save(YeuCauChiTiet entity) throws Exception
	{
		String sql = """
				INSERT OR REPLACE INTO yeu_cau_chi_tiet
				(yeu_cau_id, lop_hoc_phan_id, bat_buoc, loai_chi_dinh, trong_so)
				VALUES (?, ?, ?, ?, ?)
				""";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, entity.getYeuCauId());
			ps.setLong(2, entity.getLopHocPhanId());
			ps.setInt(3, entity.isBatBuoc() ? 1 : 0);
			ps.setString(4, entity.getLoaiChiDinh().name());

			if(entity.getTrongSo() != null)
				ps.setDouble(5, entity.getTrongSo());
			else
				ps.setNull(5, Types.REAL);

			ps.executeUpdate();
		}
	}

	@Override
	public void delete(Long yeuCauId, Long lopHocPhanId) throws Exception
	{
		String sql = """
				DELETE FROM yeu_cau_chi_tiet
				WHERE yeu_cau_id = ? AND lop_hoc_phan_id = ?
				""";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, yeuCauId);
			ps.setLong(2, lopHocPhanId);
			ps.executeUpdate();
		}
	}

	@Override
	public List<YeuCauChiTiet> findByYeuCauId(Long yeuCauId) throws Exception
	{
		String sql = """
				SELECT * FROM yeu_cau_chi_tiet
				WHERE yeu_cau_id = ?
				""";

		List<YeuCauChiTiet> list = new ArrayList<>();

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, yeuCauId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					list.add(new YeuCauChiTiet(
							rs.getLong("yeu_cau_id"),
							rs.getLong("lop_hoc_phan_id"),
							rs.getInt("bat_buoc") == 1,
							LoaiChiDinh.fromString(rs.getString("loai_chi_dinh")),
							rs.getObject("trong_so") != null
									? rs.getDouble("trong_so")
									: null));
				}
			}
		}

		return list;
	}
}
