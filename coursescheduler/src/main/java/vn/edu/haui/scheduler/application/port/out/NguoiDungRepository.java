package vn.edu.haui.scheduler.application.port.out;

import java.util.Optional;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.application.exception.DataAccessException;

public interface NguoiDungRepository
{
	Optional<NguoiDung> findByTenDangNhap(String tenDangNhap) throws DataAccessException;

	long save(NguoiDung nguoiDung) throws DataAccessException;
}
