package vn.edu.haui.scheduler.application.exception;

public class AuthenticationException extends BusinessException
{
	public AuthenticationException()
	{
		super("AUTH_INVALID_CREDENTIALS", "Invalid username or password.");
	}
}