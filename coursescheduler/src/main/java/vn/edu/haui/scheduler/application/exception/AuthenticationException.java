package vn.edu.haui.scheduler.application.exception;

public class AuthenticationException extends BusinessException
{
	public AuthenticationException()
	{
		super("AUTH_INVALID_CREDENTIALS", "Tên Đăng Nhập hoặc Mật Khẩu không hợp lệ");
	}
}