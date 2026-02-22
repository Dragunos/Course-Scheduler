package vn.edu.haui.scheduler.application.exception;

public abstract class TechnicalException extends ApplicationException
{
	private static final long serialVersionUID = 1L;

	protected TechnicalException(String errorCode, String message, Throwable cause)
	{
		super(errorCode, message, cause);
	}
}