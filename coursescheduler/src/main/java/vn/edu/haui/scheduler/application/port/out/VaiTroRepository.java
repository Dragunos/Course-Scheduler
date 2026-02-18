package vn.edu.haui.scheduler.application.port.out;

import java.util.Optional;

public interface VaiTroRepository
{
	Optional<Long> findIdByName(String roleName) throws Exception;

	Optional<String> findNameById(Long id) throws Exception;

	long save(String roleName) throws Exception;
}
