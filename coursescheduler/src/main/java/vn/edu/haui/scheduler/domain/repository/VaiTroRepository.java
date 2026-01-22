package vn.edu.haui.scheduler.domain.repository;

import java.util.Optional;

public interface VaiTroRepository
{
	Optional<Integer> timIdTheoTenVaiTro(String tenVaiTro);

	long luu(String tenVaiTro) throws Exception;
}
