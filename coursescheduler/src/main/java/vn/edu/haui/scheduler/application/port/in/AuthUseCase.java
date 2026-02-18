package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.exception.*;

public interface AuthUseCase
{
	long register(RegisterRequestDto request)
			throws ValidationException, DuplicateUsernameException, DataAccessException;

	NguoiDungDto login(LoginRequestDto request)
			throws ValidationException, AuthenticationException, DataAccessException;
}
