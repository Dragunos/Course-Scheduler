package vn.edu.haui.scheduler.application.port.out;

import java.util.Optional;
import vn.edu.haui.scheduler.domain.model.HocPhan;

public interface HocPhanRepository
{
	Optional<Long> findIdByMaHocPhan(String maHocPhan);

	Long save(HocPhan hocPhan);
}