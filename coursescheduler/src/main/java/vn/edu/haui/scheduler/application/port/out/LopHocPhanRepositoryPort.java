package vn.edu.haui.scheduler.application.port.out;

import java.util.Optional;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;

public interface LopHocPhanRepositoryPort
{
	Optional<Long> findIdByMaAndHocPhanId(String maLop, Long hocPhanId) throws Exception;

	Long save(LopHocPhan lop) throws Exception;
}
