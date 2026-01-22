package vn.edu.haui.scheduler.application.port.out;

import java.util.Optional;

public interface VaiTroRepositoryPort
{
	Optional<Integer> findIdByName(String name) throws Exception;

	long save(String name) throws Exception;
}
