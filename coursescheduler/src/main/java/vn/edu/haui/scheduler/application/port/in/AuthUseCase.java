package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.exception.*;

public interface AuthUseCase
{
	long register(DangKyRequestDto request)
			throws ValidationException, UsernameAlreadyExistsException, PersistenceException;

	NguoiDungDto login(DangNhapRequestDto request)
			throws ValidationException, AuthenticationException, PersistenceException;
}
