package vn.edu.haui.scheduler.application.exception;

public class DuplicateUsernameException extends Exception
{
	public DuplicateUsernameException()
	{
		super("username_exists");
	}
}
