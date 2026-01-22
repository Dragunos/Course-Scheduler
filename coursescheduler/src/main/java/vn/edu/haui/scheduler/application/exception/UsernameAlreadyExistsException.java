package vn.edu.haui.scheduler.application.exception;

public class UsernameAlreadyExistsException extends Exception
{
	public UsernameAlreadyExistsException()
	{
		super("username_exists");
	}
}
