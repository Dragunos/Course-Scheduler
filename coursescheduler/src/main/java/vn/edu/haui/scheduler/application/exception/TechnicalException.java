package vn.edu.haui.scheduler.application.exception;

public abstract class TechnicalException extends ApplicationException
{
	protected TechnicalException(String errorCode, String message, Throwable cause)
	{
		super(errorCode, message, cause);
	}
}