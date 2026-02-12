package vn.edu.haui.scheduler.application.port.out;

import java.util.Optional;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;

public interface LopHocPhanRepositoryPort
{
	Optional<Integer> findIdByMaAndHocPhanId(String maLop, int hocPhanId) throws Exception;

	int save(LopHocPhan lop) throws Exception;
}