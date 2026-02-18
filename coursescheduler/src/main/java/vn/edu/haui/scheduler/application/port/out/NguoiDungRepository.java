package vn.edu.haui.scheduler.application.port.out;

import java.util.Optional;
import vn.edu.haui.scheduler.domain.model.NguoiDung;

public interface NguoiDungRepository
{
	Optional<NguoiDung> findByUsername(String username) throws Exception;

	long save(NguoiDung user) throws Exception;
}
