package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.domain.model.GiangVien;

import java.util.List;
import java.util.Optional;

public interface GiangVienRepository
{
	GiangVien save(GiangVien giangVien);

	Optional<GiangVien> findById(Long id);

	Optional<GiangVien> findByTen(String tenGiangVien);

	List<GiangVien> findAll();

	void deleteById(Long id);
}