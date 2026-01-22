package vn.edu.haui.scheduler.application.service;

import java.util.Optional;

import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.repository.NguoiDungRepository;
import vn.edu.haui.scheduler.domain.repository.VaiTroRepository;
import vn.edu.haui.scheduler.infrastructure.security.PasswordHasher;

public class AuthAppService
{
	private final NguoiDungRepository nguoiDungRepo;

	private final VaiTroRepository vaiTroRepo;

	private final PasswordHasher passwordHasher;

	private final String defaultRoleName = "USER";

	public AuthAppService(NguoiDungRepository nguoiDungRepo, VaiTroRepository vaiTroRepo, PasswordHasher passwordHasher)
	{
		this.nguoiDungRepo = nguoiDungRepo;
		this.vaiTroRepo = vaiTroRepo;
		this.passwordHasher = passwordHasher;
	}

	public long register(String username, String password)
			throws ValidationException, UsernameAlreadyExistsException, PersistenceException
	{
		if(username == null || username.trim().isEmpty()) {
			throw new ValidationException("username_empty");
		}
		if(password == null || password.length() < 6) {
			throw new ValidationException("password_too_short");
		}
		String normalized = username.trim();
		try {
			Optional<NguoiDung> existing = nguoiDungRepo.timTheoTenDangNhap(normalized);
			if(existing.isPresent()) {
				throw new UsernameAlreadyExistsException();
			}

			String hash = passwordHasher.hash(password);
			long roleId = ensureDefaultRoleExists();

			NguoiDung user = new NguoiDung(normalized, hash, (int) roleId);
			long generatedId = nguoiDungRepo.luu(user);
			return generatedId;
		}
		catch(UsernameAlreadyExistsException e) {
			throw e;
		}
		catch(ValidationException e) {
			throw e;
		}
		catch(Exception e) {
			throw new PersistenceException(e);
		}
	}

	public NguoiDung login(String username, String password)
			throws ValidationException, AuthenticationException, PersistenceException
	{
		if(username == null || username.trim().isEmpty()) {
			throw new ValidationException("username_empty");
		}
		if(password == null || password.isEmpty()) {
			throw new ValidationException("password_empty");
		}

		String normalized = username.trim();

		try {
			Optional<NguoiDung> userOpt = nguoiDungRepo.timTheoTenDangNhap(normalized);
			if(userOpt.isEmpty()) {
				throw new AuthenticationException();
			}

			NguoiDung user = userOpt.get();
			boolean matched = passwordHasher.verify(password, user.getMatKhauHash());
			if(!matched) {
				throw new AuthenticationException();
			}

			return user;
		}
		catch(AuthenticationException e) {
			throw e;
		}
		catch(Exception e) {
			throw new PersistenceException(e);
		}
	}

	private long ensureDefaultRoleExists() throws PersistenceException
	{
		try {
			Optional<Integer> roleIdOpt = vaiTroRepo.timIdTheoTenVaiTro(defaultRoleName);
			if(roleIdOpt.isPresent()) {
				return roleIdOpt.get();
			}
			long created = vaiTroRepo.luu(defaultRoleName);
			return created;
		}
		catch(Exception e) {
			throw new PersistenceException(e);
		}
	}

	public static class ValidationException extends Exception
	{
		public ValidationException(String message)
		{
			super(message);
		}
	}

	public static class UsernameAlreadyExistsException extends Exception
	{
		public UsernameAlreadyExistsException()
		{
			super("username_exists");
		}
	}

	public static class PersistenceException extends Exception
	{
		public PersistenceException(Throwable cause)
		{
			super(cause);
		}
	}

	public static class AuthenticationException extends Exception
	{
		public AuthenticationException()
		{
			super("invalid_credentials");
		}
	}
}
