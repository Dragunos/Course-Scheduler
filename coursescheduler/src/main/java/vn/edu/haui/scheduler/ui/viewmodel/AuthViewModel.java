package vn.edu.haui.scheduler.ui.viewmodel;

import javafx.beans.property.*;
import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.application.exception.*;

public class AuthViewModel
{
	public enum Status
	{
		NONE, SUCCESS, ERROR
	}

	private final StringProperty username = new SimpleStringProperty("");

	private final StringProperty password = new SimpleStringProperty("");

	private final StringProperty confirm = new SimpleStringProperty("");

	private final StringProperty message = new SimpleStringProperty("");

	private final ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.NONE);

	private final IntegerProperty errorCount = new SimpleIntegerProperty(0);

	private final BooleanProperty busy = new SimpleBooleanProperty(false);

	private final AuthUseCase authService;

	public AuthViewModel(AuthUseCase authService)
	{
		this.authService = authService;
	}

	public NguoiDungDto login() throws ValidationException, AuthenticationException, PersistenceException
	{
		runGuard();
		try {
			validateLogin();

			DangNhapRequestDto request = new DangNhapRequestDto(username.get(), password.get());

			NguoiDungDto user = authService.login(request);

			onSuccess("Đăng nhập thành công");

			return user;
		}
		catch(Exception e) {
			onError(e.getMessage());
			throw e;
		}
		finally {
			busy.set(false);
		}
	}

	public void register() throws ValidationException, UsernameAlreadyExistsException, PersistenceException
	{
		runGuard();
		try {
			validateRegister();

			DangKyRequestDto request = new DangKyRequestDto(username.get(), password.get());

			authService.register(request);

			onSuccess("Đăng ký thành công");
		}
		catch(Exception e) {
			onError(e.getMessage());
			throw e;
		}
		finally {
			busy.set(false);
		}
	}

	private void onSuccess(String msg)
	{
		message.set(msg);
		status.set(Status.SUCCESS);
		errorCount.set(0);
	}

	private void onError(String msg)
	{
		message.set(msg);
		status.set(Status.ERROR);
		errorCount.set(errorCount.get() + 1);
	}

	private void runGuard()
	{
		if(busy.get()) throw new IllegalStateException("busy");

		busy.set(true);
		status.set(Status.NONE);
	}

	private void validateLogin() throws ValidationException
	{
		if(username.get().isBlank()) throw new ValidationException("Tên đăng nhập không được rỗng");
		if(password.get().isBlank()) throw new ValidationException("Mật khẩu không được rỗng");
	}

	private void validateRegister() throws ValidationException
	{
		validateLogin();

		if(password.get().length() < 6) throw new ValidationException("Mật khẩu phải ≥ 6 ký tự");
		if(!password.get().equals(confirm.get())) throw new ValidationException("Mật khẩu xác nhận không khớp");
	}

	public void clear()
	{
		username.set("");
		password.set("");
		confirm.set("");
		message.set("");
		status.set(Status.NONE);
		errorCount.set(0);
	}

	public StringProperty usernameProperty()
	{
		return username;
	}

	public StringProperty passwordProperty()
	{
		return password;
	}

	public StringProperty confirmProperty()
	{
		return confirm;
	}

	public StringProperty messageProperty()
	{
		return message;
	}

	public BooleanProperty busyProperty()
	{
		return busy;
	}

	public ObjectProperty<Status> statusProperty()
	{
		return status;
	}

	public IntegerProperty errorCountProperty()
	{
		return errorCount;
	}
}
