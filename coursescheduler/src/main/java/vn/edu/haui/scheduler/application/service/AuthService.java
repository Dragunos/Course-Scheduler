package vn.edu.haui.scheduler.application.service;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.application.port.out.NguoiDungRepository;
import vn.edu.haui.scheduler.application.port.out.VaiTroRepository;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.application.dto.RegisterRequestDto;
import vn.edu.haui.scheduler.application.dto.LoginRequestDto;
import vn.edu.haui.scheduler.application.exception.AuthenticationException;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.DuplicateUsernameException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManager;
import vn.edu.haui.scheduler.infrastructure.security.PasswordHasher;

public class AuthService implements AuthUseCase
{
	private final NguoiDungRepository nguoiDungRepo;

	private final VaiTroRepository vaiTroRepo;

	private final PasswordHasher passwordHasher;

	private final TransactionManager txManager;

	private final String defaultRoleName = "USER";

	private final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9._-]{3,50}$");

	public AuthService(
			NguoiDungRepository nguoiDungRepo,
			VaiTroRepository vaiTroRepo,
			PasswordHasher passwordHasher,
			TransactionManager txManager)
	{
		this.nguoiDungRepo = nguoiDungRepo;
		this.vaiTroRepo = vaiTroRepo;
		this.passwordHasher = passwordHasher;
		this.txManager = txManager;
	}

	@Override
	public long register(RegisterRequestDto request)
			throws ValidationException, DuplicateUsernameException, DataAccessException
	{
		if(request == null) {
			throw new ValidationException("request_null");
		}

		String tenDangNhapRaw = request.getTenDangNhap();
		String matKhau = request.getMatKhau();

		if(tenDangNhapRaw == null || tenDangNhapRaw.trim().isEmpty()) {
			throw new ValidationException("ten_dang_nhap_empty");
		}

		String tenDangNhap = tenDangNhapRaw.trim().toLowerCase();

		if(!USERNAME_PATTERN.matcher(tenDangNhap).matches()) {
			throw new ValidationException("ten_dang_nhap_invalid");
		}

		if(matKhau == null || matKhau.length() < 6) {
			throw new ValidationException("mat_khau_too_short");
		}

		if(matKhau.length() > 100) {
			throw new ValidationException("mat_khau_too_long");
		}

		txManager.begin();

		try {

			Optional<NguoiDung> existing = nguoiDungRepo.findByTenDangNhap(tenDangNhap);

			if(existing.isPresent()) {
				throw new DuplicateUsernameException();
			}

			String hash = passwordHasher.hash(matKhau);
			Long roleId = ensureDefaultRoleExists();

			NguoiDung nguoiDung = new NguoiDung(tenDangNhap, hash, roleId);

			long generatedId = nguoiDungRepo.save(nguoiDung);

			txManager.commit();
			return generatedId;
		}
		catch(Exception e) {
			txManager.rollback();

			if(isUniqueConstraintViolation(e)) {
				throw new DuplicateUsernameException();
			}

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

		String tenDangNhapRaw = request.getTenDangNhap();
		String matKhau = request.getMatKhau();

		if(tenDangNhapRaw == null || tenDangNhapRaw.trim().isEmpty()) {
			throw new ValidationException("ten_dang_nhap_empty");
		}

		if(matKhau == null || matKhau.isEmpty()) {
			throw new ValidationException("mat_khau_empty");
		}

		String tenDangNhap = tenDangNhapRaw.trim().toLowerCase();

		try {
			Optional<NguoiDung> userOpt = nguoiDungRepo.findByTenDangNhap(tenDangNhap);
			if(userOpt.isEmpty()) {
				throw new AuthenticationException();
			}

			NguoiDung user = userOpt.get();

			boolean matched = passwordHasher.verify(matKhau, user.getMatKhauHash());
			if(!matched) {
				throw new AuthenticationException();
			}

			String tenVaiTro = vaiTroRepo.findTenById(user.getVaiTroId()).orElse("UNKNOWN");

			return new NguoiDungDto(
					user.getId(),
					user.getTenDangNhap(),
					tenVaiTro,
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
			Optional<Long> roleIdOpt = vaiTroRepo.findIdByTenVaiTro(defaultRoleName);
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

	private boolean isUniqueConstraintViolation(Throwable t)
	{
		if(t == null) {
			return false;
		}
		if(t instanceof SQLException) {
			String sqlState = ((SQLException) t).getSQLState();
			if(sqlState != null && sqlState.startsWith("23")) {
				return true;
			}
		}
		String msg = t.getMessage();
		if(msg != null && (msg.contains("UNIQUE") || msg.contains("unique") || msg.contains("constraint"))) {
			return true;
		}
		return isUniqueConstraintViolation(t.getCause());
	}
}
