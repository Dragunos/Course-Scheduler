package vn.edu.haui.scheduler.application.port.out;

import java.sql.Connection;
import java.util.Optional;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;

public interface LopHocPhanRepositoryPort
{
	Optional<Integer> findIdByMaAndHocPhanId(Connection conn, String maLop, int hocPhanId) throws Exception;

	int save(Connection conn, LopHocPhan lop) throws Exception;
}