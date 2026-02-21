package vn.edu.haui.scheduler.application.exception;

public class DuplicateUsernameException extends BusinessException
{
	public DuplicateUsernameException()
	{
		super("USER_DUPLICATE_USERNAME", "Username already exists.");
	}
}