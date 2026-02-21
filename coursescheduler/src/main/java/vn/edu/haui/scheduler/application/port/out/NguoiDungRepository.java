package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.domain.model.NguoiDung;

import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository
{
	NguoiDung save(NguoiDung nguoiDung);

	Optional<NguoiDung> findById(Long id);

	Optional<NguoiDung> findByUsername(String username);

	List<NguoiDung> findAll();

	void deleteById(Long id);

	boolean existsByUsername(String username);
}