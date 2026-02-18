package vn.edu.haui.scheduler.application.port.out;

import java.util.List;
import java.util.Optional;

import vn.edu.haui.scheduler.domain.model.GiangVien;

public interface GiangVienRepository
{
	Optional<Long> findIdByTen(String tenGiangVien);

	Long save(GiangVien giangVien);

	Optional<GiangVien> findById(Long id);

	List<GiangVien> findByIds(List<Long> ids);
}
