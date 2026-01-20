package vn.edu.haui.scheduler.infrastructure.persistence;

import vn.edu.haui.scheduler.domain.model.NguoiDung;
import java.util.Optional;

public interface NguoiDungRepository
{
	Optional<NguoiDung> timTheoTenDangNhap(String tenDangNhap);

	long luu(NguoiDung nguoiDung) throws Exception;
}
