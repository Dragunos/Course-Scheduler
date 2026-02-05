package vn.edu.haui.scheduler.application.port.out;

import java.util.Optional;

public interface VaiTroRepositoryPort
{
	Optional<Integer> findIdByName(String roleName) throws Exception;

	Optional<String> findNameById(int id) throws Exception;

	long save(String roleName) throws Exception;
}
