package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.domain.model.HocPhan;

import java.util.List;
import java.util.Optional;

public interface HocPhanRepository
{
	HocPhan save(HocPhan hocPhan);

	Optional<HocPhan> findById(Long id);

	Optional<HocPhan> findByMaHocPhan(String maHocPhan);

	List<HocPhan> findAll();

	void deleteById(Long id);
}