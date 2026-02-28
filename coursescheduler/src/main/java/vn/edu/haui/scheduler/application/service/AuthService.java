package vn.edu.haui.scheduler.application.service;

import java.util.regex.Pattern;

import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.application.exception.AuthenticationException;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.application.port.out.NguoiDungRepository;
import vn.edu.haui.scheduler.application.port.out.VaiTroRepository;
import vn.edu.haui.scheduler.application.service.mapper.NguoiDungMapper;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.VaiTro;
import vn.edu.haui.scheduler.infrastructure.security.PasswordHasher;

public class AuthService implements AuthUseCase
{
	private static final String DEFAULT_ROLE_NAME = "USER";

	private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9._-]{3,50}$");

	private final NguoiDungRepository nguoiDungRepository;

	private final VaiTroRepository vaiTroRepository;

	private final PasswordHasher passwordHasher;

	public AuthService(
			NguoiDungRepository nguoiDungRepository,
			VaiTroRepository vaiTroRepository,
			PasswordHasher passwordHasher)
	{
		this.nguoiDungRepository = nguoiDungRepository;
		this.vaiTroRepository = vaiTroRepository;
		this.passwordHasher = passwordHasher;
	}

	@Override
	public NguoiDungDto register(String tenDangNhapRaw, String matKhau)
	{

		if(tenDangNhapRaw == null) {
			throw new ValidationException("Username không được phép NULL");
		}

		String tenDangNhap = tenDangNhapRaw.trim().toLowerCase();

		if(tenDangNhap.isEmpty()) {
			throw new ValidationException("Username không được để trống");
		}

		if(!USERNAME_PATTERN.matcher(tenDangNhap).matches()) {
			throw new ValidationException("Định dạng Username không hợp lệ");
		}

		if(matKhau == null || matKhau.length() < 6) {
			throw new ValidationException("Mật khẩu phải có ít nhất 6 ký tự");
		}

		VaiTro vaiTro = vaiTroRepository
				.findByTen(DEFAULT_ROLE_NAME)
				.orElseThrow(() -> new EntityNotFoundException(
						"VaiTro", "ten", DEFAULT_ROLE_NAME));

		String hash = passwordHasher.hash(matKhau);

		NguoiDung domain = NguoiDung.create(
				tenDangNhap,
				hash,
				vaiTro);

		NguoiDung saved = nguoiDungRepository.save(domain);

		return NguoiDungMapper.toDto(saved);
	}

	@Override
	public NguoiDungDto login(String tenDangNhapRaw, String matKhau)
	{
		if(tenDangNhapRaw == null) {
			throw new ValidationException("Username không được phép NULL");
		}

		String tenDangNhap = tenDangNhapRaw.trim().toLowerCase();

		if(tenDangNhap.isEmpty()) {
			throw new ValidationException("Username không được để trống");
		}

		if(matKhau == null || matKhau.isBlank()) {
			throw new ValidationException("Mật khẩu không được để trống");
		}

		NguoiDung user = nguoiDungRepository
				.findByUsername(tenDangNhap)
				.orElseThrow(AuthenticationException::new);

		if(!passwordHasher.verify(matKhau, user.getMatKhauHash())) {
			throw new AuthenticationException();
		}

		return NguoiDungMapper.toDto(user);
	}
}