package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.port.out.LopHocPhanRepositoryPort;
import vn.edu.haui.scheduler.domain.enums.HinhThucDay;
import vn.edu.haui.scheduler.domain.model.GiangVien;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JdbcLopHocPhanRepository implements LopHocPhanRepositoryPort
{

	@Override
	public Optional<Long> findIdByMaAndHocPhanId(String maLop, Long hocPhanId)
	{
		String sql = "SELECT id FROM lop_hoc_phan WHERE ma_lop = ? AND hoc_phan_id = ?";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, maLop);
			ps.setLong(2, hocPhanId);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) return Optional.of(rs.getLong("id"));
			}

			return Optional.empty();

		}
		catch(SQLException e) {
			throw new RuntimeException("Error finding LopHocPhan ID", e);
		}
	}

	@Override
	public Long save(LopHocPhan lop)
	{
		String sql = """
				INSERT INTO lop_hoc_phan
				(ma_lop, hoc_phan_id, giang_vien_id, hinh_thuc_day, dia_diem)
				VALUES (?,?,?,?,?)
				""";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, lop.getMaLop());

			if(lop.getHocPhan() != null && lop.getHocPhan().getId() != null)
				ps.setLong(2, lop.getHocPhan().getId());
			else
				ps.setNull(2, Types.BIGINT);

			if(lop.getGiangVien() != null && lop.getGiangVien().getId() != null)
				ps.setLong(3, lop.getGiangVien().getId());
			else
				ps.setNull(3, Types.BIGINT);

			if(lop.getHinhThucDay() != null)
				ps.setString(4, lop.getHinhThucDay().name());
			else
				ps.setNull(4, Types.VARCHAR);

			ps.setString(5, lop.getDiaDiem());

			int affected = ps.executeUpdate();
			if(affected == 0) {
				throw new RuntimeException("Insert lop_hoc_phan failed");
			}

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) return rs.getLong(1);
			}

			throw new RuntimeException("No ID returned after insert");

		}
		catch(SQLException e) {
			throw new RuntimeException("Error saving LopHocPhan", e);
		}
	}

	@Override
	public Optional<LopHocPhan> findById(Long id)
	{
		String sql = """
				SELECT id, ma_lop, hoc_phan_id,
				       giang_vien_id, hinh_thuc_day, dia_diem
				FROM lop_hoc_phan
				WHERE id = ?
				""";

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return Optional.of(mapRow(rs));
				}
			}

			return Optional.empty();

		}
		catch(SQLException e) {
			throw new RuntimeException("Error finding LopHocPhan by id", e);
		}
	}

	@Override
	public List<LopHocPhan> findByIds(List<Long> ids)
	{
		if(ids == null || ids.isEmpty()) return List.of();

		String placeholders = ids.stream()
				.map(i -> "?")
				.collect(Collectors.joining(","));

		String sql = """
				SELECT id, ma_lop, hoc_phan_id,
				       giang_vien_id, hinh_thuc_day, dia_diem
				FROM lop_hoc_phan
				WHERE id IN (%s)
				""".formatted(placeholders);

		List<LopHocPhan> result = new ArrayList<>();

		try (Connection conn = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			for(int i = 0; i < ids.size(); i++)
				ps.setLong(i + 1, ids.get(i));

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					result.add(mapRow(rs));
				}
			}

			return result;

		}
		catch(SQLException e) {
			throw new RuntimeException("Error finding LopHocPhan list", e);
		}
	}

	private LopHocPhan mapRow(ResultSet rs) throws SQLException
	{
		LopHocPhan lop = new LopHocPhan();

		lop.setId(rs.getLong("id"));
		lop.setMaLop(rs.getString("ma_lop"));

		Long hocPhanId = rs.getLong("hoc_phan_id");
		if(!rs.wasNull()) {
			HocPhan hp = new HocPhan();
			hp.setId(hocPhanId);
			lop.setHocPhan(hp);
		}

		Object gvObj = rs.getObject("giang_vien_id");
		if(gvObj != null) {
			Long gvId = rs.getLong("giang_vien_id");
			GiangVien gv = new GiangVien();
			gv.setId(gvId);
			lop.setGiangVien(gv);
		}

		String hinhThuc = rs.getString("hinh_thuc_day");
		if(hinhThuc != null)
			lop.setHinhThucDay(HinhThucDay.valueOf(hinhThuc));

		lop.setDiaDiem(rs.getString("dia_diem"));

		return lop;
	}
}
