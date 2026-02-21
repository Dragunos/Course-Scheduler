package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import java.util.List;
import java.util.Optional;

public interface LopHocPhanRepository
{
	LopHocPhan save(LopHocPhan lopHocPhan);

	Optional<LopHocPhan> findById(Long id);

	List<LopHocPhan> findByHocPhanId(Long hocPhanId);

	List<LopHocPhan> findAll();

	void deleteById(Long id);
}