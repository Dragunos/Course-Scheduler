package vn.edu.haui.scheduler.ui.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.application.exception.*;
import vn.edu.haui.scheduler.domain.model.NguoiDung;

public class AuthViewModel
{
	private final StringProperty username = new SimpleStringProperty("");

	private final StringProperty password = new SimpleStringProperty("");

	private final StringProperty confirm = new SimpleStringProperty("");

	private final StringProperty message = new SimpleStringProperty("");

	private final BooleanProperty busy = new SimpleBooleanProperty(false);

	private final AuthUseCase authService;

	private boolean authenticated = false;

	public AuthViewModel(AuthUseCase authService)
	{
		this.authService = authService;
	}

	public long register() throws ValidationException, UsernameAlreadyExistsException, PersistenceException
	{
		if(busy.get()) throw new IllegalStateException("busy");
		busy.set(true);
		try {
			if(username.get() == null || username.get().trim().isEmpty()) {
				throw new ValidationException("Tên đăng nhập không được rỗng");
			}
			if(password.get() == null || password.get().length() < 6) {
				throw new ValidationException("Mật khẩu phải có ít nhất 6 ký tự");
			}
			if(!password.get().equals(confirm.get())) {
				throw new ValidationException("Mật khẩu xác nhận không khớp");
			}
			long id = authService.register(username.get(), password.get());
			message.set("Đăng ký thành công (id=" + id + ")");
			return id;
		}
		finally {
			busy.set(false);
		}
	}

	public NguoiDung login() throws ValidationException, AuthenticationException, PersistenceException
	{
		if(busy.get()) throw new IllegalStateException("busy");
		busy.set(true);
		try {
			if(username.get() == null || username.get().trim().isEmpty()) {
				throw new ValidationException("Tên đăng nhập không được rỗng");
			}
			if(password.get() == null || password.get().isEmpty()) {
				throw new ValidationException("Mật khẩu không được rỗng");
			}
			NguoiDung user = authService.login(username.get(), password.get());
			authenticated = true;
			message.set("Đăng nhập thành công");
			return user;
		}
		finally {
			busy.set(false);
		}
	}

	public boolean isAuthenticated()
	{
		return authenticated;
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

	public void setMessage(String msg)
	{
		message.set(msg);
	}
}
