package vn.edu.haui.scheduler.application.exception;

public class ValidationException extends BusinessException
{
	public ValidationException(String message)
	{
		super("VALIDATION_ERROR", message);
	}
}