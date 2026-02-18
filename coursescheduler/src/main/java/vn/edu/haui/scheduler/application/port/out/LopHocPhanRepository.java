package vn.edu.haui.scheduler.application.port.out;

import java.util.List;
import java.util.Optional;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;

public interface LopHocPhanRepository
{
	Optional<Long> findIdByMaAndHocPhanId(String maLop, Long hocPhanId);

	Long save(LopHocPhan lop);

	Optional<LopHocPhan> findById(Long id);

	List<LopHocPhan> findByIds(List<Long> ids);
}
