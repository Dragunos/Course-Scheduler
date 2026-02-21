package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.domain.model.VaiTro;

import java.util.List;
import java.util.Optional;

public interface VaiTroRepository
{
	VaiTro save(VaiTro vaiTro);

	Optional<VaiTro> findById(Long id);

	Optional<VaiTro> findByTen(String tenVaiTro);

	List<VaiTro> findAll();

	void deleteById(Long id);
}