package vn.edu.haui.scheduler.infrastructure.persistence;

import java.util.Optional;

public interface VaiTroRepository
{
	Optional<Integer> timIdTheoTenVaiTro(String tenVaiTro);

	long luu(String tenVaiTro) throws Exception;
}
