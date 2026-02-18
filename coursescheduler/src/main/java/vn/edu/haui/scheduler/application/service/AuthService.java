package vn.edu.haui.scheduler.application.service;

import java.util.Optional;

import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.application.port.out.NguoiDungRepository;
import vn.edu.haui.scheduler.application.port.out.VaiTroRepository;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.exception.*;
import vn.edu.haui.scheduler.infrastructure.security.PasswordHasher;

public class AuthService implements AuthUseCase
{
	private final NguoiDungRepository nguoiDungRepo;

	private final VaiTroRepository vaiTroRepo;

	private final PasswordHasher passwordHasher;

	private final String defaultRoleName = "USER";

	public AuthService(NguoiDungRepository nguoiDungRepo, VaiTroRepository vaiTroRepo,
			PasswordHasher passwordHasher)
	{
		this.nguoiDungRepo = nguoiDungRepo;
		this.vaiTroRepo = vaiTroRepo;
		this.passwordHasher = passwordHasher;
	}

	@Override
	public long register(RegisterRequestDto request)
			throws ValidationException, DuplicateUsernameException, DataAccessException
	{
		if(request == null) {
			throw new ValidationException("request_null");
		}

		String username = request.getTenDangNhap();
		String password = request.getMatKhau();

		if(username == null || username.trim().isEmpty()) {
			throw new ValidationException("username_empty");
		}
		if(password == null || password.length() < 6) {
			throw new ValidationException("password_too_short");
		}

		String normalized = username.trim();

		try {
			Optional<NguoiDung> existing = nguoiDungRepo.findByUsername(normalized);
			if(existing.isPresent()) {
				throw new DuplicateUsernameException();
			}

			String hash = passwordHasher.hash(password);
			Long roleId = ensureDefaultRoleExists();

			NguoiDung user = new NguoiDung(normalized, hash, roleId);
			long generatedId = nguoiDungRepo.save(user);

			return generatedId;
		}
		catch(DuplicateUsernameException e) {
			throw e;
		}
		catch(ValidationException e) {
			throw e;
		}
		catch(Exception e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public NguoiDungDto login(LoginRequestDto request)
			throws ValidationException, AuthenticationException, DataAccessException
	{
		if(request == null) {
			throw new ValidationException("request_null");
		}

		String username = request.getTenDangNhap();
		String password = request.getMatKhau();

		if(username == null || username.trim().isEmpty()) {
			throw new ValidationException("username_empty");
		}
		if(password == null || password.isEmpty()) {
			throw new ValidationException("password_empty");
		}

		String normalized = username.trim();

		try {
			Optional<NguoiDung> userOpt = nguoiDungRepo.findByUsername(normalized);
			if(userOpt.isEmpty()) {
				throw new AuthenticationException();
			}

			NguoiDung user = userOpt.get();

			boolean matched = passwordHasher.verify(password, user.getMatKhauHash());
			if(!matched) {
				throw new AuthenticationException();
			}

			String roleName = vaiTroRepo.findNameById(user.getVaiTroId())
					.orElse("UNKNOWN");

			return new NguoiDungDto(
					user.getId(),
					user.getTenDangNhap(),
					roleName,
					user.getNgayTao());
		}
		catch(AuthenticationException e) {
			throw e;
		}
		catch(ValidationException e) {
			throw e;
		}
		catch(Exception e) {
			throw new DataAccessException(e);
		}
	}

	private Long ensureDefaultRoleExists() throws DataAccessException
	{
		try {
			Optional<Long> roleIdOpt = vaiTroRepo.findIdByName(defaultRoleName);
			if(roleIdOpt.isPresent()) {
				return roleIdOpt.get();
			}
			Long created = vaiTroRepo.save(defaultRoleName);
			return created;
		}
		catch(Exception e) {
			throw new DataAccessException(e);
		}
	}
}
