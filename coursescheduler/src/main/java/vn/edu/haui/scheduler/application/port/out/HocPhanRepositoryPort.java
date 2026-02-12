package vn.edu.haui.scheduler.application.port.out;

import java.util.Optional;
import vn.edu.haui.scheduler.domain.model.HocPhan;

public interface HocPhanRepositoryPort
{
	Optional<Integer> findIdByMaHocPhan(String maHocPhan) throws Exception;

	int save(HocPhan hocPhan) throws Exception;
}