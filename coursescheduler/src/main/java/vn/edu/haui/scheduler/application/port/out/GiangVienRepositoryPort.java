package vn.edu.haui.scheduler.application.port.out;

import java.sql.Connection;
import java.util.Optional;

public interface GiangVienRepositoryPort
{
	Optional<Integer> findIdByTen(Connection conn, String tenGiangVien) throws Exception;

	int save(Connection conn, String tenGiangVien) throws Exception;
}