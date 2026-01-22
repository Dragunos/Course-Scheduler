package vn.edu.haui.scheduler.application.exception;

public class AuthenticationException extends Exception
{
	public AuthenticationException()
	{
		super("invalid_credentials");
	}
}
