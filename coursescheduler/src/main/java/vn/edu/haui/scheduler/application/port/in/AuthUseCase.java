package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.application.exception.*;

public interface AuthUseCase
{
	long register(String username, String password)
			throws ValidationException, UsernameAlreadyExistsException, PersistenceException;

	NguoiDung login(String username, String password)
			throws ValidationException, AuthenticationException, PersistenceException;
}
