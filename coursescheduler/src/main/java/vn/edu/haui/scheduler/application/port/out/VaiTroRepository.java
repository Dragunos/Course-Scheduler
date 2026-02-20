package vn.edu.haui.scheduler.application.port.out;

import java.util.Optional;
import vn.edu.haui.scheduler.application.exception.DataAccessException;

public interface VaiTroRepository
{
	Optional<Long> findIdByTenVaiTro(String tenVaiTro) throws DataAccessException;

	Optional<String> findTenById(Long id) throws DataAccessException;

	long save(String tenVaiTro) throws DataAccessException;
}
