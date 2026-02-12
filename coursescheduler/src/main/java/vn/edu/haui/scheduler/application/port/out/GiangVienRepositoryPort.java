package vn.edu.haui.scheduler.application.port.out;

import java.util.Optional;

public interface GiangVienRepositoryPort
{
	Optional<Integer> findIdByTen(String tenGiangVien) throws Exception;

	int save(String tenGiangVien) throws Exception;
}