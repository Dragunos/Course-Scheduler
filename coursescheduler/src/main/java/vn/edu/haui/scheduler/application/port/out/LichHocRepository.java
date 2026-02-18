package vn.edu.haui.scheduler.application.port.out;

import java.util.List;
import java.util.Map;

import vn.edu.haui.scheduler.domain.model.LichHoc;

public interface LichHocRepository
{
	void saveAll(Long lopHocPhanId, List<LichHoc> lichHocs) throws Exception;

	List<LichHoc> findByLopHocPhanId(Long lopHocPhanId) throws Exception;

	Map<Long, List<LichHoc>> findByLopHocPhanIds(List<Long> lopHocPhanIds) throws Exception;
}
