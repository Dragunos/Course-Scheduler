package vn.edu.haui.scheduler.ui.viewmodel;

import javafx.beans.property.*;
import vn.edu.haui.scheduler.application.dto.NguoiDungDto;
import vn.edu.haui.scheduler.application.exception.*;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;

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

	private final BooleanProperty busy = new SimpleBooleanProperty(false);

	private final IntegerProperty errorCount = new SimpleIntegerProperty(0);

	private final AuthUseCase authService;

	public AuthViewModel(AuthUseCase authService)
	{
		this.authService = authService;
	}

	public NguoiDungDto login()
	{
		if(busy.get()) return null;

		busy.set(true);
		status.set(Status.NONE);

		try {
			NguoiDungDto user = authService.login(
					username.get(),
					password.get());

			onSuccess("Đăng nhập thành công");
			return user;
		}
		catch(BusinessException e) {
			onError(e.getMessage());
		}
		catch(TechnicalException e) {
			onError("Lỗi hệ thống. Vui lòng thử lại.");
		}
		finally {
			busy.set(false);
		}

		return null;
	}

	public NguoiDungDto register()
	{
		if(busy.get()) return null;

		busy.set(true);
		status.set(Status.NONE);

		try {

			if(!password.get().equals(confirm.get())) {
				throw new ValidationException("Mật khẩu xác nhận không khớp");
			}

			NguoiDungDto user = authService.register(
					username.get(),
					password.get());

			onSuccess("Đăng ký thành công");
			return user;
		}
		catch(BusinessException e) {
			onError(e.getMessage());
		}
		catch(TechnicalException e) {
			onError("Lỗi hệ thống. Vui lòng thử lại.");
		}
		finally {
			busy.set(false);
		}

		return null;
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