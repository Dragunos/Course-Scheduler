package vn.edu.haui.scheduler.application.port.out;

import java.util.Optional;

public interface GiangVienRepositoryPort
{
	Optional<Long> findIdByTen(String tenGiangVien) throws Exception;

	Long save(String tenGiangVien) throws Exception;
}