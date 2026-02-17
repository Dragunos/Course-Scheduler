package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.dto.RangBuocToiUuDto;
import vn.edu.haui.scheduler.application.port.out.RangBuocToiUuRepositoryPort;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcRangBuocToiUuRepository implements RangBuocToiUuRepositoryPort
{
	@Override
	public RangBuocToiUuDto save(RangBuocToiUuDto dto) throws Exception
	{
		if(dto.getId() == null) {
			return insert(dto);
		}
		else {
			update(dto);
			return dto;
		}
	}

	private RangBuocToiUuDto insert(RangBuocToiUuDto dto) throws Exception
	{
		String sql = """
				INSERT INTO rang_buoc_toi_uu
				(thoi_khoa_bieu_id, yeu_cau_id, loai_rang_buoc, target_type,
				 target_value, attribute, operator, value,
				 la_cung, trong_so, ghi_chu, nguoi_tao_id)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setObject(1, dto.getThoiKhoaBieuId());
			ps.setObject(2, dto.getYeuCauId());
			ps.setString(3, dto.getLoaiRangBuoc());
			ps.setString(4, dto.getTargetType());
			ps.setString(5, dto.getTargetValue());
			ps.setString(6, dto.getAttribute());
			ps.setString(7, dto.getOperator());
			ps.setString(8, dto.getValue());
			ps.setInt(9, dto.isLaCung() ? 1 : 0);
			ps.setDouble(10, dto.getTrongSo());
			ps.setString(11, dto.getGhiChu());
			ps.setObject(12, dto.getNguoiTaoId());

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(rs.next()) {
					dto.setId(rs.getLong(1));
				}
			}

			return dto;
		}
	}

	private void update(RangBuocToiUuDto dto) throws Exception
	{
		String sql = """
				UPDATE rang_buoc_toi_uu SET
				thoi_khoa_bieu_id = ?,
				yeu_cau_id = ?,
				loai_rang_buoc = ?,
				target_type = ?,
				target_value = ?,
				attribute = ?,
				operator = ?,
				value = ?,
				la_cung = ?,
				trong_so = ?,
				ghi_chu = ?,
				nguoi_tao_id = ?
				WHERE id = ?
				""";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setObject(1, dto.getThoiKhoaBieuId());
			ps.setObject(2, dto.getYeuCauId());
			ps.setString(3, dto.getLoaiRangBuoc());
			ps.setString(4, dto.getTargetType());
			ps.setString(5, dto.getTargetValue());
			ps.setString(6, dto.getAttribute());
			ps.setString(7, dto.getOperator());
			ps.setString(8, dto.getValue());
			ps.setInt(9, dto.isLaCung() ? 1 : 0);
			ps.setDouble(10, dto.getTrongSo());
			ps.setString(11, dto.getGhiChu());
			ps.setObject(12, dto.getNguoiTaoId());
			ps.setLong(13, dto.getId());

			ps.executeUpdate();
		}
	}

	@Override
	public void deleteById(Long id) throws Exception
	{
		String sql = "DELETE FROM rang_buoc_toi_uu WHERE id = ?";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, id);
			ps.executeUpdate();
		}
	}

	@Override
	public Optional<RangBuocToiUuDto> findById(Long id) throws Exception
	{
		String sql = "SELECT * FROM rang_buoc_toi_uu WHERE id = ?";

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return Optional.of(mapRow(rs));
				}
			}
			return Optional.empty();
		}
	}

	@Override
	public List<RangBuocToiUuDto> findByYeuCauId(Long yeuCauId) throws Exception
	{
		String sql = "SELECT * FROM rang_buoc_toi_uu WHERE yeu_cau_id = ?";
		return findByForeignKey(sql, yeuCauId);
	}

	@Override
	public List<RangBuocToiUuDto> findByThoiKhoaBieuId(Long thoiKhoaBieuId) throws Exception
	{
		String sql = "SELECT * FROM rang_buoc_toi_uu WHERE thoi_khoa_bieu_id = ?";
		return findByForeignKey(sql, thoiKhoaBieuId);
	}

	private List<RangBuocToiUuDto> findByForeignKey(String sql, Long id) throws Exception
	{
		List<RangBuocToiUuDto> list = new ArrayList<>();

		try (Connection connection = DataSourceProvider.getDataSource().getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					list.add(mapRow(rs));
				}
			}
		}

		return list;
	}

	private RangBuocToiUuDto mapRow(ResultSet rs) throws Exception
	{
		RangBuocToiUuDto dto = new RangBuocToiUuDto();

		dto.setId(rs.getLong("id"));
		dto.setThoiKhoaBieuId((Long) rs.getObject("thoi_khoa_bieu_id"));
		dto.setYeuCauId((Long) rs.getObject("yeu_cau_id"));
		dto.setLoaiRangBuoc(rs.getString("loai_rang_buoc"));
		dto.setTargetType(rs.getString("target_type"));
		dto.setTargetValue(rs.getString("target_value"));
		dto.setAttribute(rs.getString("attribute"));
		dto.setOperator(rs.getString("operator"));
		dto.setValue(rs.getString("value"));
		dto.setLaCung(rs.getInt("la_cung") == 1);
		dto.setTrongSo(rs.getDouble("trong_so"));
		dto.setGhiChu(rs.getString("ghi_chu"));
		dto.setNguoiTaoId((Long) rs.getObject("nguoi_tao_id"));

		return dto;
	}
}
