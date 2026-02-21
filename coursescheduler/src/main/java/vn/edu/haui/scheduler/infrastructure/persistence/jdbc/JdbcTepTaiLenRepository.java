package vn.edu.haui.scheduler.infrastructure.persistence.jdbc;

import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.out.TepTaiLenRepository;
import vn.edu.haui.scheduler.domain.model.TepTaiLen;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManagerImpl;
import vn.edu.haui.scheduler.infrastructure.persistence.jdbc.mapper.TepTaiLenJdbcMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTepTaiLenRepository implements TepTaiLenRepository
{
	private final TransactionManagerImpl transactionManager;

	public JdbcTepTaiLenRepository(TransactionManagerImpl transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	@Override
	public TepTaiLen save(TepTaiLen tepTaiLen)
	{
		if(tepTaiLen == null)
			throw new ValidationException("TepTaiLen must not be null");

		if(!tepTaiLen.isPersisted())
			return insert(tepTaiLen);

		return update(tepTaiLen);
	}

	private TepTaiLen insert(TepTaiLen tepTaiLen)
	{
		String sql = """
				INSERT INTO tep_tai_len
				(nguoi_tao_id, ten_tep_goc, loai_tep, duong_dan,
				 storage_type, file_blob, checksum, kich_thuoc, ngay_tao)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setLong(1, tepTaiLen.getNguoiTao().getId());
			ps.setString(2, tepTaiLen.getTenTepGoc());
			ps.setString(3, tepTaiLen.getLoaiTep());
			ps.setString(4, tepTaiLen.getDuongDan());
			ps.setString(5, tepTaiLen.getStorageType());

			if(tepTaiLen.getFileBlob() != null)
				ps.setBytes(6, tepTaiLen.getFileBlob());
			else
				ps.setNull(6, Types.BLOB);

			ps.setString(7, tepTaiLen.getChecksum());

			if(tepTaiLen.getKichThuoc() != null)
				ps.setLong(8, tepTaiLen.getKichThuoc());
			else
				ps.setNull(8, Types.BIGINT);

			ps.setTimestamp(9, Timestamp.valueOf(tepTaiLen.getNgayTao()));

			ps.executeUpdate();

			Long generatedId;

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if(!rs.next())
					throw new DataAccessException("Failed to retrieve TepTaiLen id", null);
				generatedId = rs.getLong(1);
			}

			return TepTaiLen.reconstruct(
					generatedId,
					tepTaiLen.getNguoiTao(),
					tepTaiLen.getTenTepGoc(),
					tepTaiLen.getLoaiTep(),
					tepTaiLen.getDuongDan(),
					tepTaiLen.getStorageType(),
					tepTaiLen.getFileBlob(),
					tepTaiLen.getChecksum(),
					tepTaiLen.getKichThuoc(),
					tepTaiLen.getNgayTao());
		}
		catch(SQLException e) {
			throw new DataAccessException("Error inserting TepTaiLen", e);
		}
	}

	private TepTaiLen update(TepTaiLen tepTaiLen)
	{
		String sql = """
				UPDATE tep_tai_len
				SET ten_tep_goc = ?, loai_tep = ?, duong_dan = ?,
				    storage_type = ?, file_blob = ?, checksum = ?, kich_thuoc = ?
				WHERE id = ?
				""";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, tepTaiLen.getTenTepGoc());
			ps.setString(2, tepTaiLen.getLoaiTep());
			ps.setString(3, tepTaiLen.getDuongDan());
			ps.setString(4, tepTaiLen.getStorageType());

			if(tepTaiLen.getFileBlob() != null)
				ps.setBytes(5, tepTaiLen.getFileBlob());
			else
				ps.setNull(5, Types.BLOB);

			ps.setString(6, tepTaiLen.getChecksum());

			if(tepTaiLen.getKichThuoc() != null)
				ps.setLong(7, tepTaiLen.getKichThuoc());
			else
				ps.setNull(7, Types.BIGINT);

			ps.setLong(8, tepTaiLen.getId());

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("TepTaiLen", tepTaiLen.getId());

			return tepTaiLen;
		}
		catch(SQLException e) {
			throw new DataAccessException("Error updating TepTaiLen", e);
		}
	}

	@Override
	public Optional<TepTaiLen> findById(Long id)
	{
		String sql = "SELECT * FROM tep_tai_len WHERE id = ?";

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if(!rs.next())
					return Optional.empty();

				return Optional.of(TepTaiLenJdbcMapper.toDomain(rs));
			}
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding TepTaiLen by id", e);
		}
	}

	@Override
	public List<TepTaiLen> findByNguoiTaoId(Long nguoiTaoId)
	{
		String sql = "SELECT * FROM tep_tai_len WHERE nguoi_tao_id = ?";

		List<TepTaiLen> result = new ArrayList<>();

		try (Connection conn = transactionManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, nguoiTaoId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					result.add(TepTaiLenJdbcMapper.toDomain(rs));
				}
			}

			return result;
		}
		catch(Exception e) {
			throw new DataAccessException("Error finding TepTaiLen by NguoiTaoId", e);
		}
	}

	@Override
	public void deleteById(Long id)
	{
		String sql = "DELETE FROM tep_tai_len WHERE id = ?";

		try (Connection conn = transactionManager.getRequiredConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			int affected = ps.executeUpdate();

			if(affected == 0)
				throw new EntityNotFoundException("TepTaiLen", id);
		}
		catch(SQLException e) {
			throw new DataAccessException("Error deleting TepTaiLen", e);
		}
	}
}