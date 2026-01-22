package vn.edu.haui.scheduler.ui.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import vn.edu.haui.scheduler.application.port.in.AuthUseCase;
import vn.edu.haui.scheduler.application.exception.*;
import vn.edu.haui.scheduler.application.service.AuthSession;
import vn.edu.haui.scheduler.domain.model.NguoiDung;

public class AuthViewModel
{
	private final StringProperty username = new SimpleStringProperty("");

	private final StringProperty password = new SimpleStringProperty("");

	private final StringProperty confirm = new SimpleStringProperty("");

	private final StringProperty message = new SimpleStringProperty("");

	private final BooleanProperty busy = new SimpleBooleanProperty(false);

	private final AuthUseCase authService;

	private final AuthSession session;

	private boolean authenticated = false;

	public AuthViewModel(AuthUseCase authService, AuthSession session)
	{
		this.authService = authService;
		this.session = session;
	}

	public void register()
	{
		if(busy.get()) return;
		busy.set(true);
		try {
			if(username.get() == null || username.get().trim().isEmpty()) {
				message.set("Tên đăng nhập không được rỗng");
				return;
			}
			if(password.get() == null || password.get().length() < 6) {
				message.set("Mật khẩu phải có ít nhất 6 ký tự");
				return;
			}
			if(!password.get().equals(confirm.get())) {
				message.set("Mật khẩu xác nhận không khớp");
				return;
			}
			long id = authService.register(username.get(), password.get());
			message.set("Đăng ký thành công (id=" + id + ")");
		}
		catch(UsernameAlreadyExistsException e) {
			message.set("Tên đăng nhập đã tồn tại");
		}
		catch(ValidationException e) {
			message.set(e.getMessage());
		}
		catch(PersistenceException e) {
			message.set("Lỗi hệ thống, vui lòng thử lại sau");
		}
		finally {
			busy.set(false);
		}
	}

	public void login()
	{
		if(busy.get()) return;
		busy.set(true);
		try {
			if(username.get() == null || username.get().trim().isEmpty()) {
				message.set("Tên đăng nhập không được rỗng");
				return;
			}
			if(password.get() == null || password.get().isEmpty()) {
				message.set("Mật khẩu không được rỗng");
				return;
			}
			NguoiDung user = authService.login(username.get(), password.get());
			session.login(user);
			authenticated = true;
			message.set("Đăng nhập thành công");
		}
		catch(AuthenticationException e) {
			authenticated = false;
			message.set("Tên đăng nhập hoặc mật khẩu không đúng");
		}
		catch(ValidationException e) {
			authenticated = false;
			message.set(e.getMessage());
		}
		catch(PersistenceException e) {
			authenticated = false;
			message.set("Lỗi hệ thống, vui lòng thử lại sau");
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
