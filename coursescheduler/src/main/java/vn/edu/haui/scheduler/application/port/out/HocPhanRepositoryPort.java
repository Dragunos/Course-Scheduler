package vn.edu.haui.scheduler.application.port.out;

import java.sql.Connection;
import java.util.Optional;
import vn.edu.haui.scheduler.domain.model.HocPhan;

public interface HocPhanRepositoryPort
{
	Optional<Integer> findIdByMaHocPhan(Connection conn, String maHocPhan) throws Exception;

	int save(Connection conn, HocPhan hocPhan) throws Exception;
}